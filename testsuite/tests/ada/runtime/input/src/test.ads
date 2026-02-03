with Ada.Calendar;          use Ada.Calendar;
with Ada.Strings.Unbounded; use Ada.Strings.Unbounded;
with Interfaces.C;

package Test is

   procedure Append (S : in out Unbounded_String; T : Time);

   function Length (S : Unbounded_String) return Interfaces.C.int
   is (Interfaces.C.int (Ada.Strings.Unbounded.Length (S)));

   function To_String (S : Unbounded_String) return String
   is (Ada.Strings.Unbounded.To_String (S));

   function Get_Time return Time
   is (Time_Of (Year => 2025, Month => 12, Day => 25, Seconds => 12345.0));

   type Time_Array is array (Positive range <>) of Time;

   procedure Append_Arr(S: in out Unbounded_String; Arr : Time_Array);

end Test;
