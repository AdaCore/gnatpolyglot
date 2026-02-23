with Withed;

package Test is

   type Rec is record
      I : Integer;
   end record;

   type A is access all Rec;

   Global_Acc : A := null;

   procedure Foo;

   procedure Bar;

end Test;
