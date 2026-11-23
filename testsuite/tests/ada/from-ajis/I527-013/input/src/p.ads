
with Base; use Base;

package P is

   type Callback is access procedure (X : in out T);

   procedure Call (C : Callback);

end P;
