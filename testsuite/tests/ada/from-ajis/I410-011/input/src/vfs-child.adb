package body VFS.Child is

   function Get (V : R) return Integer is
   begin
      return Integer (V.V);
   end Get;

end VFS.Child;
