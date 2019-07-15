package com.framework.loippi.utils.file;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.dto.oss.AliyunOss;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.FreemarkerUtils;
import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by rabook on 2015/3/18.
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    static final String success = "success";
    static final String result = "result";
    static final String fileName = "filename";
    // 最大文件大小
    private static long maxSize = 500000000;

    // 定义允许上传的文件扩展名
    private static final Map<String, String> extMap = new HashMap<String, String>();

    static {
        // 其中images,flashs,medias,files,对应文件夹名称,对应dirName
        // key文件夹名称
        // value该文件夹内可以上传文件的后缀名
        extMap.put("images", "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP");
        extMap.put("flashs", "swf,SWF,flv,FLV");
        extMap.put("medias", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,SWF,FLV,MP3,WAV,WMA,WMV,MID,AVI,MPG,ASF,RM,RMVB");
        extMap.put("files", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,DOC,DOCX,XLS,XLSX,PPT,HTM,HTML,TXT,ZIP,RAR,GZ,BZ2");
        extMap.put("sensitive", "txt,TXT");
    }


    /**
     * 上传文件-多图上传
     *
     * @param myFiles
     * @param targetDir
     * @param imgDir
     * @param type      文件格式类型
     * @param rename    是否重命名 0：原文件名 1：文件重命名
     * @return
     */
    public static Map<String, Object> fileUpload(MultipartFile[] myFiles,
                                                 String targetDir, String imgDir, HttpServletRequest request, String type, int rename) throws IOException {
        Map<String, Object> map = Maps.newHashMap();
        String originalFilename = "";
        try {
            System.out.println("imgDir======" + targetDir + imgDir);
            for (MultipartFile myFile : myFiles) {
                Map<String, Object> map1 = fileUpload(myFile, targetDir, imgDir, request, type, rename);
                if ("true".equals(map1.get(success).toString())) {
                    String imgPath = map1.get(result).toString();
                    originalFilename += imgPath + ",";
                } else {
                    map.put(success, false);
                    map.put(result, map1.get(result));
                    return map;
                }
            }
            map.put(success, true); // 成功或者失败
            if (originalFilename.length() > 0) {
                map.put(result, originalFilename.substring(0, originalFilename.length() - 1)); // 上传成功的所有的图片地址的路径
            } else {
                map.put(result, originalFilename); // 上传成功的所有的图片地址的路径
            }
        } catch (NullPointerException e) {
            map.put(success, false);
            map.put(result, "上传失败");
            e.printStackTrace();
        } catch (IOException e) {
            map.put(success, false);
            map.put(result, "上传失败");
            e.printStackTrace();
        } catch (TemplateException e) {
            map.put(success, false);
            map.put(result, "上传失败");
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 上传文件-单图上传
     *
     * @param myFile
     * @param targetDir
     * @param imgDir
     * @param type      文件格式类型
     * @param rename    是否重命名 0：原文件名 1：文件重命名
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public static Map<String, Object> fileUpload(MultipartFile myFile,
                                                 String targetDir, String imgDir, HttpServletRequest request, String type, int rename)
            throws IOException, NullPointerException, TemplateException {

        Map<String, Object> map = Maps.newHashMap();
        String originalFilename;
        map.put(success, false);
        // boolean errorFlag = true;
        // 获取内容类型
        String contentType = request.getContentType();
        int contentLength = request.getContentLength();
        // 文件保存目录路径
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("uuid", UUID.randomUUID().toString());
        String savePath = FreemarkerUtils.process(targetDir, model);
        // 文件保存目录URL
        File uploadDir = new File(savePath);
        String fileExt = myFile.getOriginalFilename().substring(myFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

        if (myFile.isEmpty()) {
            //上传图片为空
            map.put(result, "请选择文件后上传");
        } else if (!Arrays.asList(extMap.get(type).split(",")).contains(fileExt)) {// 检查扩展名
            map.put(result, "上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(type) + "格式。");
        } else if (contentType == null || !contentType.startsWith("multipart")) {
            System.out.println("请求不包含multipart/form-data流");
            map.put(result, "请求不包含multipart/form-data流");
        } else if (maxSize < contentLength) {
            System.out.println("上传文件大小超出文件最大大小");
            map.put(result, "上传文件大小超出文件最大大小[" + convertFileSize(maxSize) + "]");
        } else if (!ServletFileUpload.isMultipartContent(request)) {
            map.put(result, "请选择文件");
        } else if (!uploadDir.isDirectory()) {// 检查目录
            map.put(result, "上传目录[" + savePath + "]不存在");
        } else if (!uploadDir.canWrite()) {
            map.put(result, "上传目录[" + savePath + "]没有写权限");
        } else {
            mkdir(targetDir + imgDir);
            if (rename == 0) {
                originalFilename = myFile.getOriginalFilename();
                org.apache.commons.io.FileUtils.copyInputStreamToFile(myFile.getInputStream(),
                        new File(targetDir + imgDir, originalFilename));
                map.put("totalSize", getSize((double) myFile.getInputStream().available()));
                map.put(result, imgDir + "/" + originalFilename);
                map.put(fileName, originalFilename);
                map.put("originalfilename", myFile.getOriginalFilename());
                map.put(success, true);
            } else {
                originalFilename = String.valueOf(new Date().getTime()) +
                        myFile.getOriginalFilename().substring(myFile.getOriginalFilename().lastIndexOf("."));
                org.apache.commons.io.FileUtils.copyInputStreamToFile(myFile.getInputStream(),
                        new File(targetDir + imgDir, originalFilename));
                map.put("totalSize", getSize((double) myFile.getInputStream().available()));
                map.put(result, imgDir + "/" + originalFilename);
                map.put(fileName, originalFilename);
                map.put("originalfilename", myFile.getOriginalFilename());
                map.put(success, true);
            }
        }
        return map;
    }

    public static Map<String, Object> fileUpload(InputStream myFile, String targetDir, String imgDir)
            throws IOException, NullPointerException, TemplateException {

        Map<String, Object> map = Maps.newHashMap();
        map.put(success, false);
        // 文件保存目录路径
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("uuid", UUID.randomUUID().toString());
        String savePath = FreemarkerUtils.process(targetDir, model);
        // 文件保存目录URL
        File uploadDir = new File(savePath);
        if (!uploadDir.isDirectory()) {// 检查目录
            map.put(result, "上传目录[" + savePath + "]不存在");
        } else if (!uploadDir.canWrite()) {
            map.put(result, "上传目录[" + savePath + "]没有写权限");
        } else {
            mkdir(targetDir + imgDir);
            String originalFilename = String.valueOf(new Date().getTime()) + ".jpg";
            org.apache.commons.io.FileUtils.copyInputStreamToFile(myFile,
                    new File(targetDir + imgDir, originalFilename));
            map.put("totalSize", getSize((double) myFile.available()));
            map.put(result, imgDir + "/" + originalFilename);
            map.put(fileName, originalFilename);
            map.put("originalfilename", originalFilename);
            map.put(success, true);
        }
        return map;
    }

    /**
     * 云服务器 上传文件-多图上传
     *
     * @param myFiles
     * @param targetDir
     * @param type      文件格式类型
     * @return
     */
    public static Map<String, Object> ossfileUpload(MultipartFile[] myFiles,
                                                    String targetDir, HttpServletRequest request, String type) throws IOException {
        Map<String, Object> map = Maps.newHashMap();
        String originalFilename = "";
        try {
            System.out.println("imgDir======" + targetDir);
            for (MultipartFile myFile : myFiles) {
                Map<String, Object> map1 = ossfileUpload(myFile, targetDir, request, type);
                if ("true".equals(map1.get(success).toString())) {
                    String imgPath = map1.get(result).toString();
                    originalFilename += imgPath + ",";
                } else {
                    map.put(success, false);
                    map.put(result, map1.get(result));
                    return map;
                }
            }
            map.put(success, true); // 成功或者失败
            if (originalFilename.length() > 0) {
                map.put(result, originalFilename.substring(0, originalFilename.length() - 1)); // 上传成功的所有的图片地址的路径
            } else {
                map.put(result, originalFilename); // 上传成功的所有的图片地址的路径
            }
        } catch (NullPointerException e) {
            map.put(success, false);
            map.put(result, "上传失败");
            e.printStackTrace();
        } catch (IOException e) {
            map.put(success, false);
            map.put(result, "上传失败");
            e.printStackTrace();
        }

        return map;
    }


    /**
     * 云服务器 上传文件-单图上传
     *
     * @param myFile
     * @param targetDir
     * @param type      文件格式类型
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public static Map<String, Object> ossfileUpload(MultipartFile myFile,
                                                    String targetDir, HttpServletRequest request, String type) throws IOException, NullPointerException {

        Map<String, Object> map = Maps.newHashMap();
        String originalFilename;
        map.put(success, false);
        // boolean errorFlag = true;
        // 获取内容类型
        String contentType = request.getContentType();
        int contentLength = request.getContentLength();

        String fileExt = myFile.getOriginalFilename().substring(myFile.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

        originalFilename = String.valueOf(new Date().getTime()) + myFile.getOriginalFilename().substring(myFile.getOriginalFilename().indexOf("."));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
        }
        if (myFile.isEmpty()) {
            //上传图片为空
            map.put(result, "请选择文件后上传");
        } else if (!Arrays.asList(extMap.get(type).split(",")).contains(fileExt)) {// 检查扩展名
            map.put(result, "上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(type) + "格式。");
        } else if (contentType == null || !contentType.startsWith("multipart")) {
            System.out.println("请求不包含multipart/form-data流");
            map.put(result, "请求不包含multipart/form-data流");
        } else if (maxSize < contentLength) {
            System.out.println("上传文件大小超出文件最大大小");
            map.put(result, "上传文件大小超出文件最大大小[" + convertFileSize(maxSize) + "]");
        } else if (!ServletFileUpload.isMultipartContent(request)) {
            map.put(result, "请选择文件");
        } else {
            originalFilename = AliyunOss.uploadInputStream(originalFilename, myFile.getInputStream());
            map.put(result, originalFilename);
            map.put(success, true);
        }
        return map;
    }

    /**
     * 云服务器 上传文件-单图上传
     *
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public static Map<String, Object> ossfileUpload(InputStream inputStream) throws IOException, NullPointerException {
        Map<String, Object> map = Maps.newHashMap();
        map.put(success, false);
        String originalFilename = String.valueOf(new Date().getTime()) + ".jpg";
        originalFilename = AliyunOss.uploadInputStream(originalFilename, inputStream);
        map.put(result, originalFilename);
        map.put(fileName, originalFilename);
        map.put("originalfilename", originalFilename);
        map.put(success, true);
        // 上传图片到阿里云
        return map;
    }

    /**
     * imageFileUpload图上传
     *
     * @param myfiles
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public static Map<String, Object> imageFileUpload(String storeId, MultipartFile[] myfiles, HttpServletRequest request) throws IOException, NullPointerException {
        String type = request.getParameter("type");
        //可以在上传文件的同时接收其它参数
        Map<String, Object> map = Maps.newHashMap();
        String originalFilename = null;

        StringBuffer photoSrc = new StringBuffer();//StringBuffer用来存放上传文件的所有地址
        StringBuffer photoNewName = new StringBuffer();//用来存放图片的新名字
        StringBuffer oldName = new StringBuffer();//原来的名字
        for (MultipartFile myfile : myfiles) {
            if (myfile.isEmpty()) {
                map.put("result", "请选择文件后上传");
                map.put("success", "false");
            } else {
                originalFilename = String.valueOf(System.currentTimeMillis()) + myfile.getOriginalFilename().substring(myfile.getOriginalFilename().indexOf("."));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                }
                try {
                    String realPath = ActivityTypeEnus.FILE_BASEPATH + Constants.STORE_IMG_PATH + "/" + storeId;
                    //这里不必处理IO流关闭的问题,因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
                    //此处也可以使用Spring提供的MultipartFile.transferTo(File dest)方法实现文件的上传
                    FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, originalFilename));
                    //上传成功的时候将图片的地址给已经准备好的Stringbuffer
                    photoSrc.append(ActivityTypeEnus.FILE_BASEPATH + Constants.STORE_IMG_PATH + "/" + storeId + "/" + originalFilename + ",");
                    //上传成功的时候将图片的新名字给StringBuffer
                    photoNewName.append(originalFilename + ",");
                } catch (IOException e) {
                    map.put("result", "文件上传失败，请重试！！");
                    map.put("success", "false");

                }
            }
        }
        if ("attach".equals(type)) {
            map.put("oldName", oldName.toString());
        }
        map.put("imgPath", Constants.STORE_IMG_PATH + "/" + storeId);
        map.put("photoNewName", photoNewName.toString());
        map.put("photoSrc", photoSrc.toString());
        map.put("result", request.getContextPath() + Constants.GOODS_UPLOAD_URL + "/" + originalFilename);
        map.put("filename", originalFilename);
        map.put("success", "true");
        map.put("listimgSize", myfiles.length + "");
        return map;
    }

    /**
     * ossImageFileUpload图上传
     *
     * @param myfiles
     * @return
     * @throws IOException
     * @throws NullPointerException
     */
    public static Map<String, Object> ossImageFileUpload(String storeId, MultipartFile[] myfiles, HttpServletRequest request) throws IOException, NullPointerException {
        String type = request.getParameter("type");
        //可以在上传文件的同时接收其它参数
        Map<String, Object> map = Maps.newHashMap();
        String originalFilename = null;

        StringBuffer photoSrc = new StringBuffer();//StringBuffer用来存放上传文件的所有地址
        StringBuffer photoNewName = new StringBuffer();//用来存放图片的新名字
        StringBuffer oldName = new StringBuffer();//原来的名字
        for (MultipartFile myfile : myfiles) {
            if (myfile.isEmpty()) {
                map.put("result", "请选择文件后上传");
                map.put("success", "false");
            } else {
                originalFilename = String.valueOf(System.currentTimeMillis()) + myfile.getOriginalFilename().substring(myfile.getOriginalFilename().indexOf("."));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                }
                try {
                    String realPath = ActivityTypeEnus.FILE_BASEPATH;
                    //这里不必处理IO流关闭的问题,因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
                    //此处也可以使用Spring提供的MultipartFile.transferTo(File dest)方法实现文件的上传

                    AliyunOss.uploadInputStream(originalFilename, myfile.getInputStream());

                    //上传成功的时候将图片的地址给已经准备好的Stringbuffer
                    photoSrc.append(ActivityTypeEnus.FILE_BASEPATH + originalFilename + ",");
                    //上传成功的时候将图片的新名字给StringBuffer
                    photoNewName.append(originalFilename + ",");
                } catch (IOException e) {
                    map.put("result", "文件上传失败，请重试！！");
                    map.put("success", "false");

                }
            }
        }
        if ("attach".equals(type)) {
            map.put("oldName", oldName.toString());
        }
        map.put("imgPath", "/");
        map.put("photoNewName", photoNewName.toString());
        map.put("photoSrc", photoSrc.toString());
        map.put("result", originalFilename);
        map.put("filename", originalFilename);
        map.put("success", "true");
        map.put("listimgSize", myfiles.length + "");
        return map;
    }

    /**
     * 根据字节大小获取带单位的大小。
     *
     * @param size
     * @return
     */
    public static String getSize(double size) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (size > 1024 * 1024) {
            double ss = size / (1024 * 1024);
            return df.format(ss) + " M";
        } else if (size > 1024) {
            double ss = size / 1024;
            return df.format(ss) + " KB";
        } else {
            return size + " bytes";
        }
    }

    /**
     * 将文件路径规则化，去掉其中多余的/和\，去掉可能造成文件信息泄漏的../
     */
    public static String normalizePath(String path) {
        path = path.replace('\\', '/');
        path = FileUtils.replaceEx(path, "../", "/");
        path = FileUtils.replaceEx(path, "./", "/");
        if (path.endsWith("..")) {
            path = path.substring(0, path.length() - 2);
        }
        path = path.replaceAll("/+", "/");
        return path;
    }

    public static File normalizeFile(File f) {
        String path = f.getAbsolutePath();
        path = normalizePath(path);
        return new File(path);
    }


    /**
     * 以二进制方式读取文件
     */
    public static byte[] readByte(String fileName) {
        fileName = normalizePath(fileName);
        try {
            FileInputStream fis = new FileInputStream(fileName);
            byte[] r = new byte[fis.available()];
            fis.read(r);
            fis.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 以二进制方式读取文件
     */
    public static byte[] readByte(File f) {
        f = normalizeFile(f);
        try {

            FileInputStream fis = new FileInputStream(f);
            byte[] r = readByte(fis);
            fis.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取指定流，并转换为二进制数组
     */
    public static byte[] readByte(InputStream is) {
        try {
            byte[] r = new byte[is.available()];
            is.read(r);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制数组写入指定文件
     */
    public static boolean writeByte(String fileName, byte[] b) {
        fileName = normalizePath(fileName);
        try {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
            fos.write(b);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将二进制数组写入指定文件
     */
    public static boolean writeByte(File f, byte[] b) {
        f = normalizeFile(f);
        try {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
            fos.write(b);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 以指定编码读取指定URL中的文本
     */
    public static String readURLText(String urlPath, String encoding) {
        try {
            URL url = new URL(urlPath);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
     * 删除文件夹时会自动删除子文件夹。
     */
    public static boolean delete(String path) {
        path = normalizePath(path);
        File file = new File(path);
        return delete(file);
    }

    /**
     * 删除文件，不管路径是文件还是文件夹，都删掉。<br>
     * 删除文件夹时会自动删除子文件夹。
     */
    public static boolean delete(File f) {
        f = normalizeFile(f);
        if (!f.exists()) {
            //LogUtil.getLogger().warn("文件或文件夹不存在：" + f);
            logger.info("文件或文件夹不存在：" + f);
            return false;
        }
        if (f.isFile()) {
            //LogUtil.getLogger().info("删除："+f.getAbsolutePath());
            return f.delete();
        } else {
            return FileUtils.deleteDir(f);
        }
    }

    /**
     * 删除文件夹及其子文件夹
     */
    private static boolean deleteDir(File dir) {
        dir = normalizeFile(dir);
        try {
            //LogUtil.getLogger().info("删除："+dir);
            return deleteFromDir(dir) && dir.delete(); // 先删除完里面所有内容再删除空文件夹
        } catch (Exception e) {
            //LogUtil.getLogger().warn("删除文件夹操作出错");
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean mkdir(String path) {
        path = normalizePath(path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return true;
    }

    /**
     * 通配符方式删除指定目录下的文件或文件夹。<br>
     * 文件名支持使用正则表达式（文件路径不支持正则表达式）
     */
    public static boolean deleteEx(String fileName) {
        fileName = normalizePath(fileName);
        int index1 = fileName.lastIndexOf("\\");
        int index2 = fileName.lastIndexOf("/");
        index1 = index1 > index2 ? index1 : index2;
        String path = fileName.substring(0, index1);
        String name = fileName.substring(index1 + 1);
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (Pattern.matches(name, files[i].getName())) {
                    logger.info("删除：" + files[i].getAbsolutePath());
                    files[i].delete();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 删除文件夹里面的所有文件,但不删除自己本身
     */
    public static boolean deleteFromDir(String dirPath) {
        dirPath = normalizePath(dirPath);
        File file = new File(dirPath);
        return deleteFromDir(file);
    }

    /**
     * 删除文件夹里面的所有文件和子文件夹,但不删除自己本身
     *
     * @return
     */
    public static boolean deleteFromDir(File dir) {
        dir = normalizeFile(dir);
        if (!dir.exists()) {
            logger.info("文件夹不存在：" + dir);
            return false;
        }
        if (!dir.isDirectory()) {
            logger.info(dir + "不是文件夹");
            return false;
        }
        File[] tempList = dir.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            logger.info("删除：" + dir);
            if (!delete(tempList[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从指定位置复制文件到另一个文件夹，复制时不符合filter条件的不复制
     */
    public static boolean copy(String oldPath, String newPath, FileFilter filter) {
        oldPath = normalizePath(oldPath);
        newPath = normalizePath(newPath);
        File oldFile = new File(oldPath);
        File[] oldFiles = oldFile.listFiles(filter);
        boolean flag = true;
        if (oldFiles != null) {
            for (int i = 0; i < oldFiles.length; i++) {
                if (!copy(oldFiles[i], newPath + "/" + oldFiles[i].getName())) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 从指定位置复制文件到另一个文件夹
     */
    public static boolean copy(String oldPath, String newPath) {
        oldPath = normalizePath(oldPath);
        newPath = normalizePath(newPath);
        File oldFile = new File(oldPath);
        return copy(oldFile, newPath);
    }

    public static boolean copy(File oldFile, String newPath) {
        oldFile = normalizeFile(oldFile);
        newPath = normalizePath(newPath);
        if (!oldFile.exists()) {
            logger.info("文件或者文件夹不存在：" + oldFile);
            return false;
        }
        if (oldFile.isFile()) {
            return copyFile(oldFile, newPath);
        } else {
            return copyDir(oldFile, newPath);
        }
    }

    /**
     * 复制单个文件
     */
    private static boolean copyFile(File oldFile, String newPath) {
        oldFile = normalizeFile(oldFile);
        newPath = normalizePath(newPath);
        if (!oldFile.exists()) { // 文件存在时
            logger.info("文件不存在：" + oldFile);
            return false;
        }
        if (!oldFile.isFile()) { // 文件存在时
            logger.info(oldFile + "不是文件");
            return false;
        }
        if (oldFile.getName().equalsIgnoreCase("Thumbs.db")) {
            logger.info(oldFile + "忽略此文件");
            return true;
        }

        try {
            int byteread = 0;
            InputStream inStream = new FileInputStream(oldFile); // 读入原文件
            File newFile = new File(newPath);
            //如果新文件是一个目录，则创建新的File对象
            if (newFile.isDirectory()) {
                newFile = new File(newPath, oldFile.getName());
            }
            FileOutputStream fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            fs.close();
            inStream.close();
        } catch (Exception e) {
            logger.info("复制单个文件" + oldFile.getPath() + "操作出错。错误原因:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 复制整个文件夹内容
     */
    private static boolean copyDir(File oldDir, String newPath) {
        oldDir = normalizeFile(oldDir);
        newPath = normalizePath(newPath);
        if (!oldDir.exists()) { // 文件存在时
            logger.info("文件夹不存在：" + oldDir);
            return false;
        }
        if (!oldDir.isDirectory()) { // 文件存在时
            logger.info(oldDir + "不是文件夹");
            return false;
        }
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File[] files = oldDir.listFiles();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                temp = files[i];
                if (temp.isFile()) {
                    if (!FileUtils.copyFile(temp, newPath + "/" + temp.getName())) {
                        return false;
                    }
                } else if (temp.isDirectory()) {// 如果是子文件夹
                    if (!FileUtils.copyDir(temp, newPath + "/" + temp.getName())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.info("复制整个文件夹内容操作出错。错误原因:" + e.getMessage());
//			e.printStackTrace();
            return false;
        }
    }

    /**
     * 移动文件到指定目录
     */
    public static boolean move(String oldPath, String newPath) {
        oldPath = normalizePath(oldPath);
        newPath = normalizePath(newPath);
        return copy(oldPath, newPath) && delete(oldPath);
    }

    /**
     * 移动文件到指定目录
     */
    public static boolean move(File oldFile, String newPath) {
        oldFile = normalizeFile(oldFile);
        newPath = normalizePath(newPath);
        return copy(oldFile, newPath) && delete(oldFile);
    }

    /**
     * 将可序列化对象序列化并写入指定文件
     */
    public static void serialize(Serializable obj, String fileName) {
        fileName = normalizePath(fileName);
        try {
            FileOutputStream f = new FileOutputStream(fileName);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(obj);
            s.flush();
            s.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将可序列化对象序列化并返回二进制数组
     */
    public static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream s = new ObjectOutputStream(b);
            s.writeObject(obj);
            s.flush();
            s.close();
            return b.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从指定文件中反序列化对象
     */
    public static Object unserialize(String fileName) {
        fileName = normalizePath(fileName);
        try {
            FileInputStream in = new FileInputStream(fileName);
            ObjectInputStream s = new ObjectInputStream(in);
            Object o = s.readObject();
            s.close();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从二进制数组中反序列化对象
     */
    public static Object unserialize(byte[] bs) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bs);
            ObjectInputStream s = new ObjectInputStream(in);
            Object o = s.readObject();
            s.close();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将一个字符串中的指定片段全部替换，替换过程中不进行正则处理。<br>
     * 使用String类的replaceAll时要求片段以正则表达式形式给出，有时较为不便，可以转为采用本方法。
     */
    public static String replaceEx(String str, String subStr, String reStr) {
        if (str == null) {
            return null;
        }
        if (subStr == null || subStr.equals("") || subStr.length() > str.length() || reStr == null) {
            return str;
        }
        StringBuffer sb = new StringBuffer();
        int lastIndex = 0;
        while (true) {
            int index = str.indexOf(subStr, lastIndex);
            if (index < 0) {
                break;
            } else {
                sb.append(str.substring(lastIndex, index));
                sb.append(reStr);
            }
            lastIndex = index + subStr.length();
        }
        sb.append(str.substring(lastIndex));
        return sb.toString();
    }

    /**
     * 通过File对象创建文件
     *
     * @param file
     * @param filePath
     */
    public static void createFile(File file, String filePath) {
        int potPos = filePath.lastIndexOf('/') + 1;
        String folderPath = filePath.substring(0, potPos);
        createFolder(folderPath);
        FileOutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            fileInputStream = new FileInputStream(file);
            byte[] by = new byte[1024];
            int c;
            while ((c = fileInputStream.read(by)) != -1) {
                outputStream.write(by, 0, c);
            }
        } catch (IOException e) {
            e.getStackTrace().toString();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     *
     * @param filePath
     */
    public static void createFolder(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception ex) {
            System.err.println("Make Folder Error:" + ex.getMessage());
        }
    }

    /**
     * 获取某个文件夹下的所有文件名称和上传时间
     *
     * @param filePath
     */
    public static File[] getFileListByFilePath(String filePath) {
        File file = new File(filePath);
        File[] tempList = null;
        if (!file.exists()) {
            logger.info("文件夹不存在：" + filePath);
        } else if (!file.isDirectory()) {
            logger.info(filePath + "不是文件夹");
        } else {
            tempList = file.listFiles();
        }
        return tempList;
    }

    /**
     * 复制单个文件，如果目标文件存在，则不覆盖
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String descFileName) {
        return FileUtils.copyFileCover(srcFileName, descFileName, false);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @param coverlay     如果目标文件已存在，是否覆盖
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean copyFileCover(String srcFileName,
                                        String descFileName, boolean coverlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            logger.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
            return false;
        }
        // 判断源文件是否是合法的文件
        else if (!srcFile.isFile()) {
            logger.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
            return false;
        }
        File descFile = new File(descFileName);
        // 判断目标文件是否存在
        if (descFile.exists()) {
            // 如果目标文件存在，并且允许覆盖
            if (coverlay) {
                logger.debug("目标文件已存在，准备删除!");
                if (!FileUtils.delFile(descFileName)) {
                    logger.debug("删除目标文件 " + descFileName + " 失败!");
                    return false;
                }
            } else {
                logger.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
                return false;
            }
        } else {
            if (!descFile.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建目录
                logger.debug("目标文件所在的目录不存在，创建目录!");
                // 创建目标文件所在的目录
                if (!descFile.getParentFile().mkdirs()) {
                    logger.debug("创建目标文件所在的目录失败!");
                    return false;
                }
            }
        }

        // 准备复制文件
        // 读取的位数
        int readByte = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            // 打开源文件
            ins = new FileInputStream(srcFile);
            // 打开目标文件的输出流
            outs = new FileOutputStream(descFile);
            byte[] buf = new byte[1024];
            // 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
            while ((readByte = ins.read(buf)) != -1) {
                // 将读取的字节流写入到输出流
                outs.write(buf, 0, readByte);
            }
            logger.debug("复制单个文件 " + srcFileName + " 到" + descFileName
                    + "成功!");
            return true;
        } catch (Exception e) {
            logger.debug("复制文件失败：" + e.getMessage());
            return false;
        } finally {
            // 关闭输入输出流，首先关闭输出流，然后再关闭输入流
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException oute) {
                    oute.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ine) {
                    ine.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制整个目录的内容，如果目标目录存在，则不覆盖
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String descDirName) {
        return FileUtils.copyDirectoryCover(srcDirName, descDirName,
                false);
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirName  源目录名
     * @param descDirName 目标目录名
     * @param coverlay    如果目标目录存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectoryCover(String srcDirName,
                                             String descDirName, boolean coverlay) {
        File srcDir = new File(srcDirName);
        // 判断源目录是否存在
        if (!srcDir.exists()) {
            logger.debug("复制目录失败，源目录 " + srcDirName + " 不存在!");
            return false;
        }
        // 判断源目录是否是目录
        else if (!srcDir.isDirectory()) {
            logger.debug("复制目录失败，" + srcDirName + " 不是一个目录!");
            return false;
        }
        // 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        // 如果目标文件夹存在
        if (descDir.exists()) {
            if (coverlay) {
                // 允许覆盖目标目录
                logger.debug("目标目录已存在，准备删除!");
                if (!FileUtils.delFile(descDirNames)) {
                    logger.debug("删除目录 " + descDirNames + " 失败!");
                    return false;
                }
            } else {
                logger.debug("目标目录复制失败，目标目录 " + descDirNames + " 已存在!");
                return false;
            }
        } else {
            // 创建目标目录
            logger.debug("目标目录不存在，准备创建!");
            if (!descDir.mkdirs()) {
                logger.debug("创建目标目录失败!");
                return false;
            }

        }

        boolean flag = true;
        // 列出源目录下的所有文件名和子目录名
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 如果是一个单个文件，则直接复制
            if (files[i].isFile()) {
                flag = FileUtils.copyFile(files[i].getAbsolutePath(),
                        descDirName + files[i].getName());
                // 如果拷贝文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 如果是子目录，则继续复制目录
            if (files[i].isDirectory()) {
                flag = FileUtils.copyDirectory(files[i]
                        .getAbsolutePath(), descDirName + files[i].getName());
                // 如果拷贝目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 失败!");
            return false;
        }
        logger.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 成功!");
        return true;

    }

    /**
     * 删除文件，可以删除单个文件或文件夹
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            logger.debug(fileName + " 文件不存在!");
            return true;
        } else {
            if (file.isFile()) {
                return FileUtils.deleteFile(fileName);
            } else {
                return FileUtils.deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.debug("删除文件 " + fileName + " 成功!");
                return true;
            } else {
                logger.debug("删除文件 " + fileName + " 失败!");
                return false;
            }
        } else {
            logger.debug(fileName + " 文件不存在!");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            logger.debug(dirNames + " 目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtils.deleteFile(files[i].getAbsolutePath());
                // 如果删除文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtils.deleteDirectory(files[i]
                        .getAbsolutePath());
                // 如果删除子目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            logger.debug("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            logger.debug("删除目录 " + dirName + " 成功!");
            return true;
        } else {
            logger.debug("删除目录 " + dirName + " 失败!");
            return false;
        }

    }

    /**
     * 创建单个文件
     *
     * @param descFileName 文件名，包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            logger.debug("文件 " + descFileName + " 已存在!");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            logger.debug(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
                return false;
            }
        }

        // 创建文件
        try {
            if (file.createNewFile()) {
                logger.debug(descFileName + " 文件创建成功!");
                return true;
            } else {
                logger.debug(descFileName + " 文件创建失败!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(descFileName + " 文件创建失败!");
            return false;
        }

    }

    /**
     * 创建目录
     *
     * @param descDirName 目录名,包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            logger.debug("目录 " + descDirNames + " 已存在!");
            return false;
        }
        // 创建目录
        if (descDir.mkdirs()) {
            logger.debug("目录 " + descDirNames + " 创建成功!");
            return true;
        } else {
            logger.debug("目录 " + descDirNames + " 创建失败!");
            return false;
        }

    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     */
    public static void writeToFile(String fileName, String content, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, "utf-8", append);
            logger.debug("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            logger.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     */
    public static void writeToFile(String fileName, String content, String encoding, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, encoding, append);
            logger.debug("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            logger.debug("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param srcDirName   压缩的根目录
     * @param fileName     根目录下的待压缩的文件名或文件夹名，其中*或""表示跟目录下的全部文件
     * @param descFileName 目标zip文件
     */
    public static void zipFiles(String srcDirName, String fileName,
                                String descFileName) {
        // 判断目录是否存在
        if (srcDirName == null) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return;
        }
        File fileDir = new File(srcDirName);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            logger.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
            return;
        }
        String dirPath = fileDir.getAbsolutePath();
        File descFile = new File(descFileName);
        try {
            ZipOutputStream zouts = new ZipOutputStream(new FileOutputStream(
                    descFile));
            if ("*".equals(fileName) || "".equals(fileName)) {
                FileUtils.zipDirectoryToZipFile(dirPath, fileDir, zouts);
            } else {
                File file = new File(fileDir, fileName);
                if (file.isFile()) {
                    FileUtils.zipFilesToZipFile(dirPath, file, zouts);
                } else {
                    FileUtils
                            .zipDirectoryToZipFile(dirPath, file, zouts);
                }
            }
            zouts.close();
            logger.debug(descFileName + " 文件压缩成功!");
        } catch (Exception e) {
            logger.debug("文件压缩失败：" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
     *
     * @param zipFileName  需要解压的ZIP文件
     * @param descFileName 目标文件
     */
    public static boolean unZipFiles(String zipFileName, String descFileName) {
        String descFileNames = descFileName;
        if (!descFileNames.endsWith(File.separator)) {
            descFileNames = descFileNames + File.separator;
        }
        try {
            // 根据ZIP文件创建ZipFile对象
            ZipFile zipFile = new ZipFile(zipFileName);
            ZipEntry entry = null;
            String entryName = null;
            String descFileDir = null;
            byte[] buf = new byte[4096];
            int readByte = 0;
            // 获取ZIP文件里所有的entry
            @SuppressWarnings("rawtypes")
            Enumeration enums = zipFile.entries();
            // 遍历所有entry
            while (enums.hasMoreElements()) {
                entry = (ZipEntry) enums.nextElement();
                // 获得entry的名字
                entryName = entry.getName();
                descFileDir = descFileNames + entryName;
                if (entry.isDirectory()) {
                    // 如果entry是一个目录，则创建目录
                    new File(descFileDir).mkdirs();
                    continue;
                } else {
                    // 如果entry是一个文件，则创建父目录
                    new File(descFileDir).getParentFile().mkdirs();
                }
                File file = new File(descFileDir);
                // 打开文件输出流
                OutputStream os = new FileOutputStream(file);
                // 从ZipFile对象中打开entry的输入流
                InputStream is = zipFile.getInputStream(entry);
                while ((readByte = is.read(buf)) != -1) {
                    os.write(buf, 0, readByte);
                }
                os.close();
                is.close();
            }
            zipFile.close();
            logger.debug("文件解压成功!");
            return true;
        } catch (Exception e) {
            logger.debug("文件解压失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 将目录压缩到ZIP输出流
     *
     * @param dirPath 目录路径
     * @param fileDir 文件信息
     * @param zouts   输出流
     */
    public static void zipDirectoryToZipFile(String dirPath, File fileDir,
                                             ZipOutputStream zouts) {
        if (fileDir.isDirectory()) {
            File[] files = fileDir.listFiles();
            // 空的文件夹
            if (files.length == 0) {
                // 目录信息
                ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
                try {
                    zouts.putNextEntry(entry);
                    zouts.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // 如果是文件，则调用文件压缩方法
                    FileUtils
                            .zipFilesToZipFile(dirPath, files[i], zouts);
                } else {
                    // 如果是目录，则递归调用
                    FileUtils.zipDirectoryToZipFile(dirPath, files[i],
                            zouts);
                }
            }

        }

    }

    /**
     * 将文件压缩到ZIP输出流
     *
     * @param dirPath 目录路径
     * @param file    文件
     * @param zouts   输出流
     */
    public static void zipFilesToZipFile(String dirPath, File file,
                                         ZipOutputStream zouts) {
        FileInputStream fin = null;
        ZipEntry entry = null;
        // 创建复制缓冲区
        byte[] buf = new byte[4096];
        int readByte = 0;
        if (file.isFile()) {
            try {
                // 创建一个文件输入流
                fin = new FileInputStream(file);
                // 创建一个ZipEntry
                entry = new ZipEntry(getEntryName(dirPath, file));
                // 存储信息到压缩文件
                zouts.putNextEntry(entry);
                // 复制字节到压缩文件
                while ((readByte = fin.read(buf)) != -1) {
                    zouts.write(buf, 0, readByte);
                }
                zouts.closeEntry();
                fin.close();
                System.out
                        .println("添加文件 " + file.getAbsolutePath() + " 到zip文件中!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
     *
     * @param dirPath 目录名
     * @param file    entry文件名
     * @return
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (!dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        // 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
        if (file.isDirectory()) {
            filePath += "/";
        }
        int index = filePath.indexOf(dirPaths);

        return filePath.substring(index + dirPaths.length());
    }

    /**
     * 修复路径，将 \\ 或 / 等替换为 File.separator
     *
     * @param path
     * @return
     */
    public static String path(String path) {
        String p = StringUtils.replace(path, "\\", "/");
        p = StringUtils.join(StringUtils.split(p, "/"), "/");
        if (!StringUtils.startsWithAny(p, "/") && StringUtils.startsWithAny(path, "\\", "/")) {
            p += "/";
        }
        if (!StringUtils.endsWithAny(p, "/") && StringUtils.endsWithAny(path, "\\", "/")) {
            p = p + "/";
        }
        return p;
    }

    public static void main(String[] args) {
        File[] aaa = getFileListByFilePath("/Users/zhaorh/java/B2B2C/trunk/filebase/sensitive");
        System.out.println(aaa);
        for (File file : aaa) {
            System.out.println(file.getName());
            System.out.println(file.getPath());
            System.out.println(Dateutil.getFormatDate(new Date(file.lastModified()), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    /**
     * 文件大小转换为字符串格式
     *
     * @param size 文件大小(单位B)
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


}
