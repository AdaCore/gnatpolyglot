with Ada.Text_IO; use Ada.Text_IO;

package body Animals is

   procedure Shout (A: Parrot) is
   begin
      Put_Line ("Squak!");
   end Shout;

   procedure Repeat (A: Parrot; S: String) is
   begin
      Put_Line (S & "!");
   end Repeat;

   procedure Call_Shout (A: Animal'Class) is
   begin
      A.Shout;
   end Call_Shout;

   procedure Shout (F: Flock) is
   begin
      for I in F'Range loop
         Put ("Parrot" & I'Image 
              & " is " & F(I).C'Image & " and says: ");
         F (I).Shout;
      end loop;
   end Shout;

end Animals;
