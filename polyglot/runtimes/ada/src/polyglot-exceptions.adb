with Interfaces.C.Strings; use Interfaces.C.Strings;

package body Polyglot.Exceptions is

   ---------------------
   -- Raise_Exception --
   ---------------------

   procedure Raise_Exception
     (K        : Kernel_Access;
      Exc      : Exception_Occurrence_Access;
      Identify : Identify_Exception_Type)
   is
      Id : Exception_Id := Exception_Identity (Exc.all);
   begin
      K.Exc_Info.Exception_Id := Identify (Id);
      K.Exc_Info.Current_Exception := Exc_To_Addr (Exc);
      K.Exc_Info.Message :=
        Interfaces.C.Strings.New_String (Exception_Message (Exc.all));
      K.Exc_Info.Clear_Exception := Clear_Last_Exception'Access;
   end Raise_Exception;

   --------------------------
   -- Clear_Last_Exception --
   --------------------------

   procedure Clear_Last_Exception (K : Kernel_Access) is
      Last_Exc_Access : Exception_Occurrence_Access :=
        Addr_To_Exc (K.Exc_Info.Current_Exception);
   begin
      Exc_Free (Last_Exc_Access);
      Interfaces.C.Strings.Free (K.Exc_Info.Message);
      K.Exc_Info.Current_Exception := System.Null_Address;
   end Clear_Last_Exception;

   ------------------------------
   -- Free_Exception_Occurence --
   ------------------------------

   procedure Free_Exception_Occurence (Addr : System.Address) is
      Occurence : Exception_Occurrence_Access := Addr_To_Exc (Addr);
   begin
      Exc_Free (Occurence);
   end Free_Exception_Occurence;

end Polyglot.Exceptions;
