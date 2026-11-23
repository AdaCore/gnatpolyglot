private with VFS.Sibling;

package VFS.Child is

   type R is private;

   function Get (V : R) return Integer;

private

   type R is record
      V : VFS.Sibling.I := 190;
   end record;

end VFS.Child;
