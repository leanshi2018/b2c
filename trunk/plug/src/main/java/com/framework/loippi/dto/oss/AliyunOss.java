package com.framework.loippi.dto.oss;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * 项目名v4.1_development
 * 包名com.leimingtech.core.cloudStorage
 * 创建人kviuff
 * 创建时间16/10/8
 * 功能描述
 */
public class AliyunOss {

    // endpoint是访问OSS的域名。如果您已经在OSS的控制台上 创建了Bucket，请在控制台上查看域名。
    // 如果您还没有创建Bucket，endpoint选择请参看文档中心的“开发人员指南 > 基本概念 > 访问域名”，
    // 链接地址是：https://help.aliyun.com/document_detail/oss/user_guide/oss_concept/endpoint.html?spm=5176.docoss/user_guide/endpoint_region
    // endpoint的格式形如“http://oss-cn-hangzhou.aliyuncs.com/”，注意http://后不带bucket名称，
    // 比如“http://bucket-name.oss-cn-hangzhou.aliyuncs.com”，是错误的endpoint，请去掉其中的“bucket-name”。
    private static String endpoint = "oss-cn-hongkong.aliyuncs.com";
    private static String host = "http://yizan.oss-cn-hongkong.aliyuncs.com";

    // accessKeyId和accessKeySecret是OSS的访问密钥，您可以在控制台上创建和查看，
    // 创建和查看访问密钥的链接地址是：https://ak-console.aliyun.com/#/。
    // 注意：accessKeyId和accessKeySecret前后都没有空格，从控制台复制时请检查并去除多余的空格。
    private static String accessKeyId = "LTAIv5HAZquHrKCH";
    private static String accessKeySecret = "KBcumgpZmST5AB9iwcSL373ngXVxcH";

    // Bucket用来管理所存储Object的存储空间，详细描述请参看“开发人员指南 > 基本概念 > OSS基本概念介绍”。
    // Bucket命名规范如下：只能包括小写字母，数字和短横线（-），必须以小写字母或者数字开头，长度必须在3-63字节之间。
    private static String bucketName = "yizan";

    public static void initConfig(Map<String, Object> ossPlug) {
        endpoint = ossPlug.get("endpoint") + "";
        accessKeyId = ossPlug.get("accessKeyId") + "";
        accessKeySecret = ossPlug.get("accessKeySecret") + "";
        bucketName = ossPlug.get("bucketName") + "";
        host = "http://" + bucketName + "." + endpoint + "";
    }

    /**
     * 创建Bucket, 如果不存在创建
     *
     * @param ossClient
     * @param bucketName
     */
    public static void ensureBucket(OSSClient ossClient, String bucketName) {
        if (ossClient.doesBucketExist(bucketName)) {
            System.out.println("您已经创建Bucket：" + bucketName + "。");
        } else {
            System.out.println("您的Bucket不存在，创建Bucket：" + bucketName + "。");

            //CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucketName);
            //// 设置bucket权限
            //createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);

            ossClient.createBucket(bucketName);
        }

        // 查看Bucket信息。
        BucketInfo info = ossClient.getBucketInfo(bucketName);
        System.out.println("Bucket " + bucketName + "的信息如下：");
        System.out.println("\t数据中心：" + info.getBucket().getLocation());
        System.out.println("\t创建时间：" + info.getBucket().getCreationDate());
        System.out.println("\t用户标志：" + info.getBucket().getOwner());
    }


    /**
     * 上传字符串
     *
     * @param bucketName 字符串的存储空间
     * @param key        字符串的键
     * @param content    字符串的值
     */
    public static void uploadStr(String bucketName, String key, String content) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传文件流
     *
     * @param bucketName 文件的存储空间
     * @param key        文件的键
     * @param localFile  文件路径
     */
    public static String uploadInputStreamFile(String bucketName, String key, String localFile) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件流
            InputStream inputStream = new FileInputStream(localFile);

            ossClient.putObject(bucketName, key, inputStream);
            //返回图片路径
            URL url = ossClient.generatePresignedUrl(bucketName, key, new Date());
            return url.toString();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * 上传文件流
     *
     * @param key         文件的键
     * @param inputStream 文件流
     */
    public static String uploadInputStream(String key, InputStream inputStream) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String url = "";
        try {
            // 创建Bucket
            //ensureBucket(ossClient, bucketName);
            // 上传文件流
            ossClient.putObject(bucketName, key, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ossClient.shutdown();
        }
        return host + "/" + key;
    }

    /**
     * 上传文件
     *
     * @param bucketName 文件的存储空间
     * @param key        文件的键
     * @param localFile  文件路径
     */
    public static void uploadLocalFile(String bucketName, String key, String localFile) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件
            File file = new File(localFile);
            ossClient.putObject(bucketName, key, file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 创建模拟文件夹
     *
     * @param bucketName 文件的存储空间
     * @param folderName 文件夹名称
     */
    public static void uploadFolder(String bucketName, String folderName) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 上传文件夹
            final String keySuffixWithSlash = folderName;
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 断点续传
     *
     * @param bucketName 文件的存储空间
     * @param key        文件的键
     * @param localFile  文件路径
     */
    public static void uploadCheckPoint(String bucketName, String key, String localFile) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建Bucket
            ensureBucket(ossClient, bucketName);
            // 设置断点续传请求
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            // 指定上传的本地文件
            uploadFileRequest.setUploadFile(localFile);
            // 指定上传并发线程数
            uploadFileRequest.setTaskNum(5);
            // 指定上传的分片大小
            uploadFileRequest.setPartSize(1 * 1024 * 1024);
            // 开启断点续传
            uploadFileRequest.setEnableCheckpoint(true);
            // 断点续传上传
            ossClient.uploadFile(uploadFileRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 分片上传
     *
     * @param bucketName
     * @param key
     * @param localFile
     */
    public static void uploadSliceUpload(String bucketName, String key, String localFile) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 初始化Multipart Upload
            // 用InitiateMultipartUploadRequest指定上传文件的名字和所属存储空间（Bucket）；
            // 在InitiateMultipartUploadRequest中，您也可以设置ObjectMeta；
            // initiateMultipartUpload 的返回结果中含有UploadId，它是区分分片上传事件的唯一标识，在后面的操作中将用到它。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
            String uploadId = result.getUploadId();

            // 上传分片
            // 注意：
            // UploadPart 方法要求除最后一个Part以外，其他的Part大小都要大于100KB。但是Upload Part接口并不会立即校验上传Part的大小（因为不知道是否为最后一块）；
            // 只有当Complete Multipart Upload的时候才会校验。
            // OSS会将服务器端收到Part数据的MD5值放在ETag头内返回给用户。
            // 为了保证数据在网络传输过程中不出现错误，SDK会自动设置Content-MD5，OSS会计算上传数据的MD5值与SDK计算的MD5值比较，如果不一致返回InvalidDigest错误码。
            // Part号码的范围是1~10000。如果超出这个范围，OSS将返回InvalidArgument的错误码。
            // 每次上传part时都要把流定位到此次上传块开头所对应的位置。
            // 每次上传part之后，OSS的返回结果会包含一个 PartETag 对象，他是上传块的ETag与块编号（PartNumber）的组合，
            // 在后续完成分片上传的步骤中会用到它，因此我们需要将其保存起来。一般来讲我们将这些 PartETag 对象保存到List中。
            List<PartETag> partETags = new ArrayList<PartETag>();
            InputStream instream = null;
            instream = new FileInputStream(new File("<localFile>"));
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(key);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小，除最后一个分片外，其它分片要大于100KB
            uploadPartRequest.setPartSize(100 * 1024);
            // 设置分片号，范围是1~10000，
            uploadPartRequest.setPartNumber(1);
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            partETags.add(uploadPartResult.getPartETag());

            // 完成分片上传
            // 注意：
            // 代码中的 partETags 就是进行分片上传中保存的partETag的列表，它必须是按分片号升序排序；
            // 分片可以是不连续的。
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag p1, PartETag p2) {
                    return p1.getPartNumber() - p2.getPartNumber();
                }
            });
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }

    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param key
     * @param localFile
     */
    public static void downFileToLocation(String bucketName, String key, String localFile) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 下载object到文件
            ossClient.getObject(new GetObjectRequest(bucketName, key), new File(localFile));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 删除单个文件
     *
     * @param bucketName
     * @param key
     */
    public static void deleteFile(String bucketName, String key) {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 下载object到文件
            ossClient.deleteObject(bucketName, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * oss生成二维码
     *
     * @param text      二维码内容
     * @param imagename 图片名称
     * @return
     */
    public static String uploadQRcord2Oss(String text, String imagename) {
        // http://show-oss-chebian.oss-cn-shenzhen.aliyuncs.com/store-details/802331103296766000_CASE1
        text = "https://a.app.qq.com/o/simple.jsp?pkgname=com.ewhale.distribution";
        int width = 300;
        int height = 300;
        // 二维码的图片格式
        String format = "png";
        String s="";
        /**
         * 设置二维码的参数
         */
        HashMap<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        String nameString = "";
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            // 生成二维码流
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            // 转为流文件
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
             s = uploadInputStream(imagename, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


}
