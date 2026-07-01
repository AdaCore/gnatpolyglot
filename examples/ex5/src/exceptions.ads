-- This example showcases the use of exceptions: when the bound library raises
-- an exception, which is propagated in the target language, and how overrides
-- from the user are also able to throw exceptions to the bound library.

package Exceptions is

   Exc1 : exception;
   Exc2 : exception;

   type Tag is tagged null record;

   procedure Raise_Exc (B : Boolean);

   procedure Raise_Exc (T : Tag);

   procedure Call_Raise_Exc (T : Tag'Class);

   procedure Raise_Constraint;

end Exceptions;
