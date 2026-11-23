package Test is

   procedure Proc
     (Callback : access function (S : String) return Boolean := null);

end Test;
