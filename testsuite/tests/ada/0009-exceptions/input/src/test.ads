with Other;

package Test is

   Exc1, Exc2 : exception;

   ExcR : exception renames Other.Exc1;

   procedure Get_Exception (I: Integer);

end Test;
