#ifndef PGM_TO_ARRAY_H_INCLUDED
#define PGM_TO_ARRAY_H_INCLUDED

#include <stdio.h>
#include <stdlib.h>

#define IMAGE_SIZE 25  // 25x25 image
#define TOTAL_PIXELS (IMAGE_SIZE * IMAGE_SIZE)

void read_and_normalize_pgm(const char *filename, float pixels[TOTAL_PIXELS]) {
    FILE *file = fopen(filename, "rb");  // Open in binary mode
    if (!file) {
        perror("Error opening file");
        exit(EXIT_FAILURE);
    }

    // Variables to store metadata
    char magic_number[3];
    int width, height, max_value;

    // Read the magic number (P5)
    if (!fgets(magic_number, sizeof(magic_number), file) || magic_number[0] != 'P' || magic_number[1] != '5') {
        fprintf(stderr, "Invalid or unsupported PGM file format\n");
        fclose(file);
        exit(EXIT_FAILURE);
    }

    // Read image dimensions and max pixel value
    if (fscanf(file, "%d %d %d%*c", &width, &height, &max_value) != 3) {  // %*c skips the newline
        fprintf(stderr, "Error reading PGM header\n");
        fclose(file);
        exit(EXIT_FAILURE);
    }

    // Check dimensions
    if (width != IMAGE_SIZE || height != IMAGE_SIZE) {
        fprintf(stderr, "Unexpected image dimensions: %dx%d (expected %dx%d)\n", width, height, IMAGE_SIZE, IMAGE_SIZE);
        fclose(file);
        exit(EXIT_FAILURE);
    }

    // Allocate buffer for pixel data
    unsigned char *buffer = (unsigned char *)malloc(TOTAL_PIXELS * sizeof(unsigned char));
    if (!buffer) {
        perror("Memory allocation error");
        fclose(file);
        exit(EXIT_FAILURE);
    }

    // Read the binary pixel data
    if (fread(buffer, sizeof(unsigned char), TOTAL_PIXELS, file) != TOTAL_PIXELS) {
        fprintf(stderr, "Error reading pixel data\n");
        free(buffer);
        fclose(file);
        exit(EXIT_FAILURE);
    }

    // Normalize pixel values and store in the float array
    for (int i = 0; i < TOTAL_PIXELS; i++) {
        pixels[i] = buffer[i] / (float)max_value;
    }

    // Cleanup
    free(buffer);
    fclose(file);
    printf("done.");
}

#endif // PGM_TO_ARRAY_H_INCLUDED
