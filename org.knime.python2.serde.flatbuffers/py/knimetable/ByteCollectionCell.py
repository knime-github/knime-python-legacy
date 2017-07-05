# automatically generated by the FlatBuffers compiler, do not modify

# namespace: flatc

import flatbuffers

class ByteCollectionCell(object):
    __slots__ = ['_tab']

    @classmethod
    def GetRootAsByteCollectionCell(cls, buf, offset):
        n = flatbuffers.encode.Get(flatbuffers.packer.uoffset, buf, offset)
        x = ByteCollectionCell()
        x.Init(buf, n + offset)
        return x

    # ByteCollectionCell
    def Init(self, buf, pos):
        self._tab = flatbuffers.table.Table(buf, pos)

    # ByteCollectionCell
    def Value(self, j):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            x = self._tab.Vector(o)
            x += flatbuffers.number_types.UOffsetTFlags.py_type(j) * 4
            x = self._tab.Indirect(x)
            from .ByteCell import ByteCell
            obj = ByteCell()
            obj.Init(self._tab.Bytes, x)
            return obj
        return None

    # ByteCollectionCell
    def ValueLength(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            return self._tab.VectorLen(o)
        return 0

    # ByteCollectionCell
    def Missing(self, j):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
        if o != 0:
            a = self._tab.Vector(o)
            return self._tab.Get(flatbuffers.number_types.BoolFlags, a + flatbuffers.number_types.UOffsetTFlags.py_type(j * 1))
        return 0

    # ByteCollectionCell
    def MissingLength(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
        if o != 0:
            return self._tab.VectorLen(o)
        return 0

    # ByteCollectionCell
    def KeepDummy(self):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(8))
        if o != 0:
            return self._tab.Get(flatbuffers.number_types.BoolFlags, o + self._tab.Pos)
        return 0
    
    # custom method
    # Returns all values as collection. All strings are decoded (utf-8).
    # @pram islist    true - returns a list, false - returns a set
    def GetAllValues(self, islist):
        o = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(4))
        if o != 0:
            from .ByteCell import ByteCell
            l = self.ValueLength()
            # Add values
            if islist:
                buff = []
                for j in range(l):
                    x = self._tab.Vector(o)
                    x += flatbuffers.number_types.UOffsetTFlags.py_type(j) * 4
                    x = self._tab.Indirect(x)
                    obj = ByteCell()
                    obj.Init(self._tab.Bytes, x)
                    buff.append(obj.GetAllBytes())
                # Handle missing values
                o2 = flatbuffers.number_types.UOffsetTFlags.py_type(self._tab.Offset(6))
                if o2 != 0:
                    a2 = self._tab.Vector(o2)
                    m = self.MissingLength()
                    for j in range(m):
                        if self._tab.Get(flatbuffers.number_types.BoolFlags, a2 + j):
                            buff[j] = None
                    return buff
                return None
            else:
                buff = set()
                for j in range(l):
                    x = self._tab.Vector(o)
                    x += flatbuffers.number_types.UOffsetTFlags.py_type(j) * 4
                    x = self._tab.Indirect(x)
                    obj = ByteCell()
                    obj.Init(self._tab.Bytes, x)
                    buff.add(obj.GetAllBytes())
                # Handle missing values
                if self.KeepDummy():
                    buff.append(None)
                return buff
        return None

def ByteCollectionCellStart(builder): builder.StartObject(3)
def ByteCollectionCellAddValue(builder, value): builder.PrependUOffsetTRelativeSlot(0, flatbuffers.number_types.UOffsetTFlags.py_type(value), 0)
def ByteCollectionCellStartValueVector(builder, numElems): return builder.StartVector(4, numElems, 4)
def ByteCollectionCellAddMissing(builder, missing): builder.PrependUOffsetTRelativeSlot(1, flatbuffers.number_types.UOffsetTFlags.py_type(missing), 0)
def ByteCollectionCellStartMissingVector(builder, numElems): return builder.StartVector(1, numElems, 1)
def ByteCollectionCellAddKeepDummy(builder, keepDummy): builder.PrependBoolSlot(2, keepDummy, 0)
def ByteCollectionCellEnd(builder): return builder.EndObject()
