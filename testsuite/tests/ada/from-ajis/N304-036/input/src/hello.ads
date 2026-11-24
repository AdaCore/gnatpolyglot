------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2014, AdaCore                          --
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

   type R_E is abstract tagged private;
   type G_E is new R_E with private;
   function PGI (E : R_E) return Integer is abstract;

--   type R_E is abstract tagged null record;
--   function PGI (E : R_E) return Integer;
--   type G_E is new R_E with record
--      X : Integer := 1961;
--   end record;
--   overriding function PGI (E : G_E) return Integer;

private
   type R_E is abstract tagged null record;
   type G_E is new R_E with record
      X : Integer := 1961;
   end record;
   overriding function PGI (E : G_E) return Integer;

end Hello;
