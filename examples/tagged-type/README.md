This example showcases the use of tagged types in the bound library, how to
inherit from such types across the language barriers, and override behaviour.

To run the example, use the Makefile at the root of the example and run
the `ada2proxy` rule to generate the Ada glue, then either one of the
`proxy2{target}` to generate the glue code for the `{target}` language.
To compile and run the main executable, run the `{target}` rule.
