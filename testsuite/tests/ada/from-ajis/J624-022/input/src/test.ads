with Gen;

package Test is
   
   type Root is abstract tagged null record;

   package Cont is new Gen (Root'Class);
   
end Test;
