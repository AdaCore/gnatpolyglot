package body Lists is

   procedure Push(L: in out List; Item : List_Item_Access) is
      It : List_Item_Access := L.First;
   begin
      if It = null then
         L.First := Item;
      else
         while It.Next /= null loop
            It := It.Next;
         end loop;
         It.Next := Item;
      end if;
   end Push;

   function Get (L : List; I : Natural) return List_Item_Access is
      Index : Natural := 0;
      It : List_Item_Access := L.First;
   begin
      while Index /= I and then It /= null loop
         It := It.Next;
         Index := Index + 1;
      end loop;
      return It;
   end Get;

end Lists;

