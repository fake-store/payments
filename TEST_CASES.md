# Payments Service — Test Cases

## Unit Test Scope

---

### PaymentService

#### `processPayment(request: UserPaymentRequest)`

| # | Test Case | Description |
|---|-----------|-------------|
| 1 | Idempotency check — already processed | Given a `userPaymentRequestId` that exists in the repository, `processPayment` should return the existing result and NOT call any processor or emit any Kafka message. |
| 2 | Processor selection — CreditCard | Given a payment request where the method type is `CREDIT_CARD`, the `CreditCardProcessor` is selected and its `charge()` is called. |
| 3 | Processor selection — each type | Parameterized: repeat for DEBIT_CARD, ACH, PAYPAL, APPLE_PAY, GOOGLE_PAY — each routes to the correct concrete processor. |
| 4 | Payment method not found | If `findPaymentMethodsByUserId` returns no method matching `userPaymentMethodId`, the payment is rejected with a meaningful error. |
| 5 | Processor throws `PaymentRejectedException` | Processor rejection is caught, saved as REJECTED status, and the rejection Kafka message is published (not the processed topic). |
| 6 | Processor throws generic `RuntimeException` | Exception is re-thrown so Kafka can retry; no payment record is saved. |
| 7 | Successful payment saved | After a successful `charge()`, a `Payment` record is saved to the repository with correct fields (amount, currency, method reference). |
| 8 | Kafka `processed` topic called on success | `KafkaMessageSender` is called with the `payments.payment.processed` topic after successful charge. |
| 9 | Kafka `rejected` topic called on rejection | `KafkaMessageSender` is called with the `payments.payment.rejected` topic on `PaymentRejectedException`. |
| 10 | Auto-provision payment methods | If `findPaymentMethodsByUserId` returns empty list, 3 random methods are created and returned. |

#### `addPaymentMethod(userId, request)`

| # | Test Case | Description |
|---|-----------|-------------|
| 11 | Saves with correct userId | The new `UserPaymentMethod` is persisted with the supplied `userId`. |
| 12 | First method added becomes default | If no methods exist for the user, the new method is set `isDefault = true`. |
| 13 | Second method added, not default | If user already has methods, new method is `isDefault = false`. |

#### `updatePaymentMethod(userId, methodId, request)`

| # | Test Case | Description |
|---|-----------|-------------|
| 14 | Label updated | `label` is changed to the new value. |
| 15 | Set as default — clears previous default | When `isDefault = true`, any previously-default method for the same user is set to `false`. |
| 16 | Clear default — does not touch others | When `isDefault = false`, no other methods are modified. |
| 17 | Method not found | Returns/throws appropriate error when `methodId` does not exist for `userId`. |

#### `deletePaymentMethod(userId, methodId)`

| # | Test Case | Description |
|---|-----------|-------------|
| 18 | Method removed from repository | After delete, `findByUserId` no longer includes the method. |
| 19 | Wrong userId cannot delete | Attempting to delete a method belonging to a different user fails gracefully. |

#### `getRecentPayments(userId, limit)`

| # | Test Case | Description |
|---|-----------|-------------|
| 20 | Returns at most `limit` records | With 10 payments in repo and `limit=3`, only 3 are returned. |
| 21 | Ordered newest first | Results are sorted by `createdAt` descending. |

---

### PaymentProcessor (abstract + concrete)

#### `processPayment(request, method)` (template method)

| # | Test Case | Description |
|---|-----------|-------------|
| 22 | Calls `describe()` then `charge()` | Template method invokes `describe` before `charge`; both must be called in order. |
| 23 | `describe()` includes amount and method label | Returned string contains the formatted amount and the method's label. |

#### `CreditCardProcessor.charge()` / `DebitCardProcessor.charge()` / etc.

| # | Test Case | Description |
|---|-----------|-------------|
| 24 | Returns `PaymentResult.SUCCESS` on simulated success | When `PaymentSimulator` returns success, `charge()` returns `SUCCESS`. |
| 25 | Throws `PaymentRejectedException` on simulated rejection | When `PaymentSimulator` returns rejection, `charge()` throws `PaymentRejectedException`. |
| 26 | `supportedType` matches class | Each processor's `supportedType` matches the expected `PaymentMethodTypeEnum` value. |

---

### KafkaMessageListener

#### `onPaymentRequestMessageReceived(record)`

| # | Test Case | Description |
|---|-----------|-------------|
| 27 | TraceId extracted from header, placed in MDC | When a Kafka record arrives with a `traceId` header, that value is present in `MDC.get("traceId")` during processing. |
| 28 | TraceId removed from MDC after processing | After the listener method returns, `MDC.get("traceId")` is null (no leakage between messages). |
| 29 | OrderId placed in MDC during processing | `orderId` from the message body is placed in `MDC.get("orderId")` during `processPayment`. |
| 30 | Successful processing — ACK called | On successful payment, the Kafka `Acknowledgment.acknowledge()` is called. |
| 31 | `PaymentRejectedException` — ACK called | Rejected payment is a valid terminal state; `acknowledge()` still called so message is not retried. |
| 32 | Generic exception — NOT acknowledged | On unexpected exception, ACK is not called, triggering Kafka retry. |
| 33 | Log contains `[TID=<uuid>]` on receipt | The log line emitted at message receipt contains the trace ID in the expected log pattern. |
| 34 | Log contains `[OID=<uuid>]` during processing | The orderId log prefix appears in log output during `processPayment`. |

---

### KafkaMessageSender

| # | Test Case | Description |
|---|-----------|-------------|
| 35 | TraceId written to Kafka header | The outbound `ProducerRecord` headers include `traceId` matching current MDC value. |
| 36 | OrderId written to Kafka header | Headers include `orderId`. |
| 37 | PaymentId written to Kafka header | Headers include `paymentId`. |
| 38 | Topic routed correctly for PROCESSED | `payments.payment.processed` is the destination for a successful payment message. |
| 39 | Topic routed correctly for REJECTED | `payments.payment.rejected` is the destination for a rejected payment message. |
| 40 | Send failure is logged quietly, not thrown | If the Kafka send callback receives an exception, it is logged but the exception is not propagated. |

---

### PaymentController

| # | Test Case | Description |
|---|-----------|-------------|
| 41 | `GET /api/payments/methods` returns 200 with methods list | Valid token returns the user's payment methods. |
| 42 | `GET /api/payments/methods` — no auth returns 401 | Missing or invalid Bearer token results in HTTP 401. |
| 43 | TraceId from `X-Trace-Id` header placed in MDC | When controller receives `X-Trace-Id: <uuid>`, that value appears in MDC during handler execution. |
| 44 | TraceId removed from MDC after response | MDC is clean after the response is written. |
| 45 | `POST /api/payments/methods` creates method | Returns 201 with created method body. |
| 46 | `POST /api/payments/methods/{id}/delete` — wrong user returns 404 | Cannot delete another user's method. |
| 47 | `GET /api/payments/history` returns ordered list | Returns payment history for the authenticated user. |

---

### JwtUtil

| # | Test Case | Description |
|---|-----------|-------------|
| 48 | `validateAndGetClaims` returns correct userId | A valid signed JWT returns a map containing the correct `userId` string. |
| 49 | Expired token returns null | A JWT with past expiry returns null (no exception leaks). |
| 50 | Tampered signature returns null | A JWT with a modified signature returns null. |
| 51 | Wrong secret returns null | A JWT signed with a different key returns null. |

---

## Logging Assertions

These tests verify that log output contains the expected MDC-enriched prefixes.

| # | Trigger | Expected Log Content |
|---|---------|---------------------|
| L1 | Kafka message received | `[TID=<uuid>]` appears in the INFO log line "Received payment request" |
| L2 | During `processPayment` | `[TID=<uuid>][OID=<uuid>]` both appear in processor log lines |
| L3 | Payment saved | `[PID=<uuid>]` appears once `paymentId` is assigned and logged |
| L4 | Kafka send callback | `[TID=<uuid>][OID=<uuid>][PID=<uuid>]` all present in send-success log |
| L5 | Kafka retry scenario | After generic exception, MDC is NOT leaked to the retry attempt (fresh MDC) |
| L6 | HTTP controller request | `[TID=<uuid>]` appears in controller handler log if `X-Trace-Id` header supplied |

**Implementation note:** Use a test `Appender<ILoggingEvent>` (e.g. `ListAppender` from Logback) to capture log output and assert on formatted messages.

---

## Suggested Test Tools (Payments)

- **JUnit 5** + **MockK** (Kotlin-native mocking)
- **Logback `ListAppender`** for log content assertions
- **EmbeddedKafka** (`@EmbeddedKafka`) for listener tests that need a real broker
- **Spring `@WebMvcTest`** for controller-layer tests
- **Spring `@SpringBootTest`** for full-context integration tests
