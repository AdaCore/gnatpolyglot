package Lists is
   type List_Item;
   type List_Item_Access is access all List_Item;
   type List_Item is record
      Value : Integer;
      Next  : List_Item_Access := null;
   end record;

   type List is record
      First : List_Item_Access;
   end record;

   procedure Push (L : in out List; Item : List_Item_Access);

   function Get (L : List; I : Natural) return List_Item_Access;

end Lists;
