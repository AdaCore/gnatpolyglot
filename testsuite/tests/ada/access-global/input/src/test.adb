package body Test is

   procedure Foo is
   begin
      if Global_Acc /= null then
         Global_Acc.all.I := Global_Acc.all.I + 1;
      end if;
   end Foo;

   procedure Bar is
   begin
      Global_Acc := null;
   end Bar;

end Test;
