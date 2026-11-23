package VFS is

   type Virtual_File is tagged null record;

   type File_Array is array (Positive range <>) of Virtual_File;

   procedure Prim (X :  Virtual_File; Y : File_Array);

end VFS;
