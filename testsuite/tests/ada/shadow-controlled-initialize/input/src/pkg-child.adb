with Ada.Text_IO; use Ada.Text_IO;

package body Pkg.Child is

   overriding procedure Initialize (Self : in out Pub_Override) is
   begin
      Put_Line ("Initialize Pub_Override");
   end Initialize;

end Pkg.Child;
