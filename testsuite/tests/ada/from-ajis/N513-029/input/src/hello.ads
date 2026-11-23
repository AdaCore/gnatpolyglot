------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2014-2018, AdaCore                     --
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

   type Root_Entity is abstract tagged null record;
   type Root_Entity_Reference is abstract tagged null record;
   type On_Callee_Function is
     access function (Callee : Root_Entity'Class) return Boolean;
   procedure For_Each_Dispatching_Call
     (Ref       : Root_Entity_Reference;
      On_Callee : On_Callee_Function)
   is abstract;

   type General_Entity_Reference is new Root_Entity_Reference with null record;
   overriding procedure For_Each_Dispatching_Call
     (Ref       : General_Entity_Reference;
      On_Callee : On_Callee_Function);

end Hello;
