package Pkg is

   --  Each Replace procedure stores a new value allocated by Ada in A

   --  String (bound to PolyglotString)

   type String_Access is access all String;
   procedure Replace (A : in out String_Access);

   --  Arrays of native types (bound to the runtime array classes)

   type Int_Array is array (Positive range <>) of Integer;
   type Int_Array_Access is access all Int_Array;
   procedure Replace (A : in out Int_Array_Access);

   type Byte_Array is array (Positive range <>) of Short_Short_Integer;
   type Byte_Array_Access is access all Byte_Array;
   procedure Replace (A : in out Byte_Array_Access);

   type Short_Array is array (Positive range <>) of Short_Integer;
   type Short_Array_Access is access all Short_Array;
   procedure Replace (A : in out Short_Array_Access);

   type Long_Array is array (Positive range <>) of Long_Long_Integer;
   type Long_Array_Access is access all Long_Array;
   procedure Replace (A : in out Long_Array_Access);

   type Float_Array is array (Positive range <>) of Float;
   type Float_Array_Access is access all Float_Array;
   procedure Replace (A : in out Float_Array_Access);

   type Double_Array is array (Positive range <>) of Long_Float;
   type Double_Array_Access is access all Double_Array;
   procedure Replace (A : in out Double_Array_Access);

   type Bool_Array is array (Positive range <>) of Boolean;
   type Bool_Array_Access is access all Bool_Array;
   procedure Replace (A : in out Bool_Array_Access);

   --  Array of a record type (bound to a generated array class)

   type Rec is record
      V : Integer;
   end record;

   type Rec_Array is array (Positive range <>) of Rec;
   type Rec_Array_Access is access all Rec_Array;
   procedure Replace (A : in out Rec_Array_Access);

end Pkg;
