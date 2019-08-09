class Object
  def to_s
    ""
  end
end

class NilClass < Object
  def to_a
    []
  end

  def to_h
    {}
  end

  def to_i
    0
  end

  def to_f
    0.0
  end
end

class Array < Object
  def to_a
    self
  end

  def to_h
    {}
  end

  def first
    self[0]
  end

  def last
    self[0]
  end

  def map(&block)
    [block.call(self[0])]
  end

  def each(&block)
    [block.call(self[0])]
    self
  end

  def each_with_index(&block)
    [block.call(self[0], 0)]
    self
  end

  def count
    0
  end

  def length
    0
  end

  def find(&block)
    r = self[0]
    if block.call(r)
      r
    else
      nil
    end
  end

  def find_all
    self
  end
end

class Hash < Object
  def to_a
    []
  end

  def to_h
    self
  end

  def count
    0
  end

  def length
    0
  end
end

class TrueClass < Object
end

class FalseClass < Object
end

class Numeric < Object
  def to_i
    0
  end

  def to_f
    0.0
  end
end

class Float < Numeric
end

class Integer < Numeric
end

class String < Object
  def length
    0
  end
end

class Symbol < Object
  def length
    0
  end
end
