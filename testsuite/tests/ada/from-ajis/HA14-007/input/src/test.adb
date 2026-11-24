package body Test is

   procedure Fill (This : Root; Str : in out String) is
   begin
      Str := (others => This.Filling);
   end Fill;

end Test;
