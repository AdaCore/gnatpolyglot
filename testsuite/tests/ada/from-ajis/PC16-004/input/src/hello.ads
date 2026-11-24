------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2017, AdaCore                          --
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

package Hello is

   type Record_Type is record
      I : Integer := 1;
   end record;
   procedure Say (P : Record_Type);

   type Tagged_Record_Type is tagged record
      I : Integer := 1;
   end record;
   function Msg (P : Tagged_Record_Type) return String;
   procedure Say (P : Tagged_Record_Type'Class);

   subtype Binding_Renaming_Type is Hello.Tagged_Record_Type;

end Hello;
