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

package body Xref_Root_Reference_Iterator is

   ---------------------
   -- Should_Be_Bound --
   ---------------------

   procedure Should_Be_Bound (P1 : Boolean; P2 : access Root_Entity'Class) is
   begin
      null;
   end Should_Be_Bound;

   -------------------------
   -- Find_All_References --
   -------------------------

   overriding function Find_All_References
     (Entity                : General_Entity;
      In_Scope              : Root_Entity'Class := No_Root_Entity)
      return Root_Reference_Iterator'Class
   is
   begin
      return No_Reference_Iterator;
   end Find_All_References;

   ------------
   -- At_End --
   ------------

   overriding function At_End
     (Iter : Dummy_Reference_Iterator)
      return Boolean
   is
   begin
      return True;
   end At_End;

end Xref_Root_Reference_Iterator;
