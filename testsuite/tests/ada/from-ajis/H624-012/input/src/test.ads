------------------------------------------------------------------------------
--                                 ADA2JAVA-TEST                            --
--                                                                          --
--                     Copyright (C) 2016, AdaCore                          --
--                                                                          --
-- This is free software;  you can redistribute it  and/or modify it  under --
-- terms of the  GNU General Public License as published  by the Free Soft- --
-- ware  Foundation;  either version 3,  or (at your option) any later ver- --
-- sion.  This software is distributed in the hope  that it will be useful, --
-- but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHAN- --
-- TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public --
-- License for  more details.  You should have  received  a copy of the GNU --
-- General  Public  License  distributed  with  this  software;   see  file --
-- COPYING3.  If not, go to http://www.gnu.org/licenses for a complete copy --
-- of the license.                                                          --
------------------------------------------------------------------------------

with Ada.Finalization; use Ada.Finalization;

package Test is

   type A_Type is tagged private;

   type A_Type_Acc is access all A_Type'Class;

   function F_1 return A_Type'Class;

   function F_2 return A_Type_Acc;

   Display_Finalize : Boolean := False;

   Expected_Freed : Integer := 0;

private

   type A_Type is new Controlled with record
      Should_Be_Freed : Boolean;
   end record;

   procedure Initialize (V : in out A_Type);

   procedure Finalize (V : in out A_Type);

   procedure Adjust (V : in out A_Type);

end Test;
