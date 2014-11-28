class Helper
  def initialize(data)
    @title = "Haml from boot?"
    @data = data
  end

  def data_string
    "What the #{@data[:what_the]}?"
  end
end
