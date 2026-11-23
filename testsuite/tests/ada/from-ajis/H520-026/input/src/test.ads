package Test is

   type Proc is access procedure (Text : String);

   procedure Do_Something (P : Proc);

end Test;