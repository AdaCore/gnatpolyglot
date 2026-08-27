This example showcases the use of exceptions, and that they can be raised by
the library to be caught by the used, or thrown by the user and caught by the
library during an upcall to the user code.

To run the example, use the Makefile at the root of the example and run
the `ada2proxy` rule to generate the Ada glue, then either one of the
`proxy2{target}` to generate the glue code for the `{target}` language.
To compile and run the main executable, run the `{target}` rule.
