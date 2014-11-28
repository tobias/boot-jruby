require 'haml'

$: << File.dirname(__FILE__)

require 'helper'

# the $data global is set in build.boot
helper = Helper.new($data)

def slurp(file)
  File.readlines(file).join
end

def spit(file, contents)
  File.open(file, "w") do |f|
    f.write(contents)
  end
end

# the $haml_files global is set in build.boot
$haml_files.each do |f|
  spit(File.join(ENV['BOOT_RSC_PATH'], File.basename(f)[0..-6]),
       Haml::Engine.new(slurp(f)).render(helper))
end
