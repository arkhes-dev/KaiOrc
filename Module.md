# Module kaiorc

A lightweight Kotlin library for AI workflow orchestration — provider abstraction, prompt
pipelines, and structured execution.

`AIRuntime` coordinates a `Workflow` end to end: it builds an `AIExecutionContext`, runs the
workflow, and reports the outcome to an `AIObserver` — but it does no AI work itself. Everything
else (which provider answers a request, how a prompt is assembled, how a raw reply is validated,
whether a failure is worth retrying) is a small, swappable, independently testable piece:
`AIProvider`, `ContextBuilder`, `PromptBuilder`, `ResponseValidator`, `RetryPolicy`, `IntentRouter`.

Pure Kotlin/JVM — no Android, Compose, or DI-framework dependency. A host app fixes `AIResult`'s
generic error type to its own domain error type once, and wires KaiOrc's `@Inject`-annotated
classes into whatever DI container it already uses.

See the [README](https://github.com/arkhes-dev/KaiOrc#readme) for a full quick-start walkthrough.
