This example showcases the use of access types for the generated interface,
and how the memory is managed by setting the owner of a returned access.

To run the example, use the Makefile at the root of the example and run
the `ada2proxy` rule to generate the Ada glue, then either one of the
`proxy2{target}` to generate the glue code for the `{target}` language.
To compile and run the main executable, run the `{target}` rule.
