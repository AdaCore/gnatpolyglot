with Ada.Unchecked_Deallocation;
with Ada.Unchecked_Conversion;
with Ada.Exceptions;
with Polyglot.Exceptions;

package body Polyglot.Ada.Arrays is

   -----------
   -- Alloc --
   -----------

   function Alloc
     (First : Interfaces.C.Int; Last : Interfaces.C.Int) return Polyglot_Array
   is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      Arr : Arr_Type_Access := new Arr_Type (First .. Last);
   begin
      return (First => First, Last => Last, Data => Arr.all'Address);
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
      return (0, 0, System.Null_Address);
   end Alloc;

   ---------------
   -- Construct --
   ---------------

   procedure Construct
     (Self : System.Address; First : Interfaces.C.Int; Last : Interfaces.C.Int)
   is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      Arr        : Arr_Type_Access := new Arr_Type (First .. Last);
      Self_Value : Polyglot_Array
      with Address => Self;
   begin
      Self_Value := (First => First, Last => Last, Data => Arr.all'Address);
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
   end Construct;

   -----------
   -- Clone --
   -----------

   function Clone (Self : Polyglot_Array) return Polyglot_Array is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      subtype Arr_Type_C is Arr_Type (Self.First .. Self.Last);
      type Arr_Type_C_Access is access all Arr_Type_C
      with Size => Standard'Address_Size;

      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_C_Access);
      Data_Access : Arr_Type_C_Access := Address_Converter (Self.Data);

      Arr : Arr_Type_Access := new Arr_Type'(Data_Access.all);
      Res : Polyglot_Array :=
        (First => Self.First, Last => Self.Last, Data => Arr.all'Address);
   begin
      return Res;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
      return (0, 0, System.Null_Address);
   end Clone;

   ----------
   -- Copy --
   ----------

   procedure Copy (To : System.Address; From : Polyglot_Array) is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      subtype Arr_Type_C is Arr_Type (From.First .. From.Last);
      type Arr_Type_C_Access is access all Arr_Type_C
      with Size => Standard'Address_Size;

      To_Value : Polyglot_Array
      with Address => To;
      pragma Import (Ada, To_Value);

      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_C_Access);
      Data_Access : Arr_Type_C_Access := Address_Converter (From.Data);
      Arr         : Arr_Type_Access := new Arr_Type'(Data_Access.all);
   begin
      To_Value.First := From.First;
      To_Value.Last := From.Last;
      To_Value.Data := Arr.all'Address;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
   end Copy;

   ----------
   -- Free --
   ----------

   procedure Free (Self : System.Address) is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;

      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      procedure Arr_Free is new
        Standard.Ada.Unchecked_Deallocation (Arr_Type, Arr_Type_Access);

      Self_Value : Polyglot_Array
      with Address => Self;
      pragma Import (Ada, Self_Value);

      Data_Access : Arr_Type_Access := Address_Converter (Self_Value.Data);
   begin
      Arr_Free (Data_Access);
      Self_Value.Data := System.Null_Address;
   end Free;

   ---------
   -- Get --
   ---------

   function Get
     (Self : Polyglot_Array; Index : Interfaces.C.Int) return System.Address
   is
      type Arr_Type is array (Self.First .. Self.Last) of C;
      type Arr_type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);
   begin
      return Data_Access.all (Index)'Address;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
      return System.Null_Address;
   end Get;

   ----------------
   -- Get_Access --
   ----------------

   function Get_Access
     (Self : Polyglot_Array; Index : Interfaces.C.Int) return System.Address
   is
      type Arr_Type is array (Self.First .. Self.Last) of C;
      type Arr_type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);
      function Access_Converter is new
        Standard.Ada.Unchecked_Conversion (C, System.Address);
   begin
      return Access_Converter (Data_Access.all (Index));
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
      return System.Null_Address;
   end Get_Access;

   ---------
   -- Set --
   ---------

   procedure Set (Self : Polyglot_Array; Index : Interfaces.C.Int; New_Val : C)
   is
      type Arr_Type is array (Self.First .. Self.Last) of C;
      type Arr_type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);
   begin
      Data_Access.all (Index) := New_Val;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
   end Set;

   ----------------
   -- Set_Record --
   ----------------

   procedure Set_Record
     (Self    : Polyglot_Array;
      Index   : Interfaces.C.Int;
      New_Val_Addr : System.Address)
   is
      type Arr_Type is array (Self.First .. Self.Last) of C;
      type Arr_type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);
      type C_Access is access all C
      with Size => Standard'Address_Size;
      function C_Access_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, C_Access);
      New_Val : C_Access := C_Access_Converter (New_Val_Addr);
   begin
      Data_Access.all (Index) := New_Val.all;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
   end Set_Record;

   ----------------
   -- Set_Access --
   ----------------

   procedure Set_Access
     (Self    : Polyglot_Array;
      Index   : Interfaces.C.Int;
      New_Val_Addr : System.Address)
   is
      type Arr_Type is array (Self.First .. Self.Last) of C;
      type Arr_type_Access is access all Arr_Type
      with Size => Standard'Address_Size;
      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);
      function C_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, C);
      New_Val : C := C_Converter (New_Val_Addr);
   begin
      Data_Access.all (Index) := New_Val;
   exception
      when E: others =>
         Polyglot.Exceptions.Raise_Exception
           (E, Polyglot.Exceptions.Identify_Standard_Exception'Access);
   end Set_Access;

end Polyglot.Ada.Arrays;
