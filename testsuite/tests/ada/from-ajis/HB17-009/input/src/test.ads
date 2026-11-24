
package Test is

   procedure Call
     (V : access procedure
        (V1 : Integer; V2 : out Integer; V3 : in out Integer));

end Test;
