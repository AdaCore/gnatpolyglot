import com.adacore.libtest.xref_abstract_entities_cursor.AbstractEntitiesCursor;
import com.adacore.libtest.xref_abstract_entities_cursor.XrefAbstractEntitiesCursorPackage;
import com.adacore.libtest.xref_parameter_array.XrefParameterArrayPackage;
import com.adacore.libtest.xref_root_reference_iterator.RootReferenceIterator;
import com.adacore.libtest.xref_root_reference_iterator.XrefRootReferenceIteratorPackage;

public class Main {
    public static void main(String[] args) {
        XrefParameterArrayPackage.shouldBeBound(true, XrefParameterArrayPackage.getNoRootEntity());
        XrefParameterArrayPackage.shouldBeBound(true, XrefParameterArrayPackage.getNoRootEntity());
        XrefRootReferenceIteratorPackage.shouldBeBound(true,
                XrefRootReferenceIteratorPackage.getNoRootEntity());
        XrefAbstractEntitiesCursorPackage.shouldBeBound(true,
                XrefAbstractEntitiesCursorPackage.getNoRootEntity());

        RootReferenceIterator iter = XrefRootReferenceIteratorPackage.getNoRootEntity()
                .findAllReferences(XrefRootReferenceIteratorPackage.getNoRootEntity());
        if (iter.atEnd()) {
            AbstractEntitiesCursor cursor = XrefAbstractEntitiesCursorPackage.getNoRootEntity()
                    .getAllCalledEntities();
            if (cursor.atEnd()) {
                var params = XrefParameterArrayPackage.getNoRootEntity().parameters();
                if (params.getUnslided(params.getBegin()).getParameter()) {
                    System.out.println("OK");
                }
            }
        }
    }
}
