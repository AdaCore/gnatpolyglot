with Hello_Impl;  use Hello_Impl;

package Hello is

   type T is abstract new T_Impl with null record;

   procedure Say (Msg : String);

end Hello;
