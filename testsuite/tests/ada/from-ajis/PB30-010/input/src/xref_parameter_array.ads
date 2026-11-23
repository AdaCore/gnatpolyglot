------------------------------------------------------------------------------
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

package Xref_Parameter_Array is

   type Parameter_Array;

   type Root_Entity is abstract tagged null record;
   No_Root_Entity : aliased constant Root_Entity'Class;
   --  aliased is added to let AJIS make it accessible to GNATbench

   function Parameters
     (Entity : Root_Entity) return Parameter_Array is abstract;
   --  Return the list of parameters for a given subprogram

   type General_Parameter is record
      Parameter : Boolean;
   end record;

   type Parameter_Array is
     array (Natural range <>) of aliased General_Parameter;

   procedure Should_Be_Bound (P1 : Boolean; P2 : access Root_Entity'Class);

private

   type General_Entity is new Root_Entity with record
      Is_Fuzzy   : Boolean := False;  --  Whether this is a fuzzy match
   end record;

   overriding function Parameters
     (Entity : General_Entity) return Parameter_Array;

   No_General_Entity : aliased constant General_Entity :=
     (Is_Fuzzy   => False);

   No_Root_Entity : aliased constant Root_Entity'Class :=
     No_General_Entity;

end Xref_Parameter_Array;
