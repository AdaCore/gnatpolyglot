with Ada.Text_IO; use Ada.Text_IO;

package body Pkg is

   overriding procedure Initialize (Self : in out Pub) is
   begin
      Put_Line ("Initialize Pub");
   end Initialize;

   overriding procedure Initialize (Self : in out Pub_Value) is
   begin
      Put_Line ("Initialize Pub_Value");
   end Initialize;

   overriding procedure Initialize (Self : in out Priv) is
   begin
      Put_Line ("Initialize Priv");
   end Initialize;

end Pkg;
