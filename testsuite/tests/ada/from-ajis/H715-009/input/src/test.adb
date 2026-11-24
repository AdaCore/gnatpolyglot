package body Test is

   function Copy (V : A_T) return A_T is
   begin
      return new T'Class'(V.all);
   end Copy;

end Test;