package Test is

    protected type Prot is
    end Prot;

    subtype ST is Prot;
    type STA is access all ST;

    type Unsupp (S : STA) is private;

private

    type Unsupp (S : STA) is null record;

end Test;
