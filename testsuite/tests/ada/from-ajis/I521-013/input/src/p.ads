with Gen;
with Gen_Proc;

package P is
   package Inst_1 is new Gen (Integer);

   type My_Rec is record
      Val : Integer;
   end record;

   package Inst_2 is new Gen (My_Rec);

   function Create return Inst_2.R;

   function Inst_Proc is new Gen_Proc (My_Rec);

end P;