with Ada.Unchecked_Deallocation;
with Ada.Unchecked_Conversion;

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
   end Construct;

   -----------
   -- Clone --
   -----------

   function Clone (Self : Polyglot_Array) return Polyglot_Array is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;

      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (Self.Data);

      Arr : Arr_Type_Access := new Arr_Type'(Data_Access.all);
      Res : Polyglot_Array :=
        (First => Self.First, Last => Self.Last, Data => Arr.all'Address);
   begin
      return Res;
   end Clone;

   ----------
   -- Copy --
   ----------

   procedure Copy (To : System.Address; From : Polyglot_Array) is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      type Arr_Type_Access is access all Arr_Type
      with Size => Standard'Address_Size;

      To_Value : Polyglot_Array
      with Address => To;
      pragma Import (Ada, To_Value);

      function Address_Converter is new
        Standard.Ada.Unchecked_Conversion (System.Address, Arr_Type_Access);
      Data_Access : Arr_Type_Access := Address_Converter (From.Data);
      Arr         : Arr_Type_Access := new Arr_Type'(Data_Access.all);
   begin
      To_Value.First := From.First;
      To_Value.Last := From.Last;
      To_Value.Data := Arr.all'Address;
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
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      Self_Value : Arr_Type (Self.First .. Self.Last)
      with Address => Self.Data;
      pragma Import (Ada, Self_Value);
   begin
      return Self_Value (Index)'Address;
   end Get;

   ---------
   -- Set --
   ---------

   procedure Set (Self : Polyglot_Array; Index : Interfaces.C.Int; New_Val : C)
   is
      type Arr_Type is array (Interfaces.C.Int range <>) of C;
      Self_Value : Arr_Type (Self.First .. Self.Last)
      with Address => Self.Data;
      pragma Import (Ada, Self_Value);
   begin
      Self_Value (Index) := New_Val;
   end Set;

end Polyglot.Ada.Arrays;
