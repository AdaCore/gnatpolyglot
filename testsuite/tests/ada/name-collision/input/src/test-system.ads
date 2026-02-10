package Test.System is
   -- The System package may conflict with Test.Systme in the proxy package.
   -- When detected, a renaming package should shadow Test.Package to still be
   -- able to refer to Standard.System.

   type Foo is null record;

end Test.System;
