package gallery;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private static final Integer PAGE_SIZE = 20;
    private List<File> photos;

    public MainController() {
        photos = new ArrayList<>();
    }

    @RequestMapping("/")
    public String main(@RequestParam(value="page", required=false, defaultValue="1") Integer page, ModelMap model) {
        model.addAttribute("photosForPage", getPhotosForPage(page));
        return "main";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    public List<File> getPhotosForPage(Integer pageNumber) {
        if (pageNumber - 1 > photos.size() / PAGE_SIZE) {
            pageNumber = 1;
        }

        List<File> photosForPage = new ArrayList<>();
        Integer lastPhotoNumber = pageNumber * PAGE_SIZE > photos.size() ? photos.size() : pageNumber * PAGE_SIZE;

        for (int i = (pageNumber - 1) * PAGE_SIZE; i < lastPhotoNumber; i++) {
            photosForPage.add(photos.get(i));
        }

        return photosForPage;
    }

    private void store(MultipartFile inputFile) {
        try {
            File outputFile = new File("target/photos/" + inputFile.getOriginalFilename().replaceAll(" ", ""));
            outputFile.mkdirs();
            if (outputFile.createNewFile()) {
                inputFile.transferTo(outputFile);
                photos.add(outputFile);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
