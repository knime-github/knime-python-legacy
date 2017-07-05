# automatically generated by the FlatBuffers compiler, do not modify

# namespace: flatc

import flatbuffers

class DoubleCollectionColumn(object):
    __slots__ = ['_tab']

    @classmethod
    def GetRootAsDoubleCollectionColumn(cls, buf, offset):
        n = flatbuffers.encode.Get(flatbuffers.packer.uoffset, buf, offset)
        x = DoubleCollectionColumn()
        x.Init(buf, n + offset)
        return x

    # DoubleCollectionColumn
    def Init(self, buf, pos):
        self._tab = flatbuffers.table.Table(buf, pos)

    # DoubleCollectionColumn
    def Values(self, j):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            x = self._tab.Vector(o)
            x += flatbuffers.number_types.UOffsetTFlags.py_type(j) * 4
            x = self._tab.Indirect(x)
            from .DoubleCollectionCell import DoubleCollectionCell
            obj = DoubleCollectionCell()
            obj.Init(self._tab.Bytes, x)
            return obj
        return None

    # DoubleCollectionColumn
    def ValuesLength(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            return self._tab.VectorLen(o)
        return 0

    # DoubleCollectionColumn
    def Missing(self, j):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
        if o != 0:
            a = self._tab.Vector(o)
            return self._tab.Get(flatbuffers.number_types.BoolFlags, a + flatbuffers.number_types.UOffsetTFlags.py_type(j * 1))
        return 0

    # DoubleCollectionColumn
    def MissingLength(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
        if o != 0:
            return self._tab.VectorLen(o)
        return 0
    
    # custom method
    # Puts all values in this flatbuffers-column into a dataframe column.
    # @param df        a dataframe (preinitialized)
    # @param colidx    the index of the column to set (in the dataframe)
    # @param islist    true - collection type is list, false - set
    def AddValuesAsColumn(self, df, colidx, islist):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            from .DoubleCollectionCell import DoubleCollectionCell
            l = self.ValuesLength()
            # Add values
            for j in range(l):
                x = self._tab.Vector(o)
                x += flatbuffers.number_types.UOffsetTFlags.py_type(j) * 4
                x = self._tab.Indirect(x)
                obj = DoubleCollectionCell()
                obj.Init(self._tab.Bytes, x)
                df.iat[j, colidx] = obj.GetAllValues(islist) 
            # Handle missing values
            o2 = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
            if o2 != 0:
                a2 = self._tab.Vector(o2)
                m = self.MissingLength()
                for j in range(m):
                    if self._tab.Get(flatbuffers.number_types.BoolFlags, a2 + j):
                        df.iat[j, colidx] = None
                return True
            return False
        return False

def DoubleCollectionColumnStart(builder): builder.StartObject(2)
def DoubleCollectionColumnAddValues(builder, values): builder.PrependUOffsetTRelativeSlot(0, flatbuffers.number_types.UOffsetTFlags.py_type(values), 0)
def DoubleCollectionColumnStartValuesVector(builder, numElems): return builder.StartVector(4, numElems, 4)
def DoubleCollectionColumnAddMissing(builder, missing): builder.PrependUOffsetTRelativeSlot(1, flatbuffers.number_types.UOffsetTFlags.py_type(missing), 0)
def DoubleCollectionColumnStartMissingVector(builder, numElems): return builder.StartVector(1, numElems, 1)
def DoubleCollectionColumnEnd(builder): return builder.EndObject()
