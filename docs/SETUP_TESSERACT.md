## Setup Tesseract Locally

This project uses Tesseract, an open source OCR tool
to extract data from PDFs and Images.

This is an important dependency, since the whole verification
pipeline is dependent on the extraction part.

### Steps

Here are the steps you need to do in order to setup this:

- Install the binaries required for Tesseract. For different OS,
there are different ways to download this:
    - **macOS**: 
      - Go to the `Terminal`.
      - Run the following command:
      ```bash 
      brew install tesseract
      ```
      - This will install Tesseract and run it in background.
    - **Windows**:
      - Use the `.exe` installer from the official 
      webpage and run this, once installed.
      - Update the required environment variables and you
      are done!
    - **Linux**:
      - Run this command (for Ubuntu):
      ```bash 
      sudo apt install tesseract-ocr libtesseract-dev
      ```
      (For other distributions, refer to their respective installers.)

- Once done, you need to install the training data from the web.
For this project, the data is [here](https://github.com/tesseract-ocr/tessdata_best/blob/main/eng.traineddata). 
  - Once downloaded, save it in the `src/main/resources/tessdata` folder.
    (Create the `tessdata` folder, it is not default!)

### Confirmation

To confirm if tesseract is working, you can run the service. If tesseract does not throw an error during extraction,
it means that the setup is working fine.