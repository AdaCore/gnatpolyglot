This example showcases a simple hello world function and how to call such
bound subprograms.

To run the example, use the Makefile at the root of the example and run
the `ada2proxy` rule to generate the Ada glue, then either one of the
`proxy2{target}` to generate the glue code for the `{target}` language.
To compile and run the main executable, run the `{target}` rule.
