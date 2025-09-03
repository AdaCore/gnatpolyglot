with System;

package Polyglot.Ada is
   type Shadow_Data is record
      Self   : System.Address;
      Vtable : System.Address;
   end record;
end Polyglot.Ada;
