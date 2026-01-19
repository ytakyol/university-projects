import os
from PIL import Image

# Define input and output folder paths
input_folder = r"HeShe\male"
output_folder = r"HeShe\male3"

# Ensure the output folder exists
os.makedirs(output_folder, exist_ok=True)

# Loop through each .png file in the input folder
for filename in os.listdir(input_folder):
    if filename.endswith(".png"):
        # Open the .png file
        img = Image.open(os.path.join(input_folder, filename))
        
        # Convert image to greyscale (if not already)
        img = img.convert("L")
        
        # Get the pixel values
        pixels = list(img.getdata())
        width, height = img.size

        # Construct P2 PGM format content
        pgm_content = f"P2\n{width} {height}\n255\n"
        pgm_content += "\n".join(
            " ".join(str(pixels[y * width + x]) for x in range(width))
            for y in range(height)
        )

        # Write the content to a .pgm file
        pgm_filename = os.path.join(output_folder, filename.replace(".png", ".pgm"))
        with open(pgm_filename, "w") as pgm_file:
            pgm_file.write(pgm_content)

print("Conversion completed!")
