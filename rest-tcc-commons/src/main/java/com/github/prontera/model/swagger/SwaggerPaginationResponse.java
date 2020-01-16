package com.github.prontera.model.swagger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhao Junjian
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class SwaggerPaginationResponse<T> implements Serializable {

    private static final long serialVersionUID = 5753340671258665259L;

    @ApiModelProperty(value = "页号(客户端指定, 页号从1作为起始页)", required = true, example = "3")
    @JsonProperty("page_num")
    private int pageNum;

    @ApiModelProperty(value = "页面大小(客户端指定)", required = true, example = "30")
    @JsonProperty("page_size")
    private int pageSize;

    @ApiModelProperty(value = "实际页面大小", required = true, example = "18")
    @JsonProperty("size")
    private int size;

    @ApiModelProperty(value = "排序方向", example = "CREATE_TIME DESC")
    @JsonProperty("order_by")
    private String orderBy;

    @ApiModelProperty(value = "当前页面第一个元素在数据库中的行号", example = "63")
    @JsonProperty("start_row")
    private int startRow;

    @ApiModelProperty(value = "当前页面最后一个元素在数据库中的行号", example = "81")
    @JsonProperty("end_row")
    private int endRow;

    @ApiModelProperty(value = "总的项目数", required = true, example = "81")
    @JsonProperty("total")
    private long total;

    @ApiModelProperty(value = "总页面数", required = true, example = "3")
    @JsonProperty("pages")
    private int pages;

    @ApiModelProperty(value = "详细信息", required = true)
    @JsonProperty("list")
    private List<T> list;

    @ApiModelProperty(value = "首页页号", required = true, example = "1")
    @JsonProperty("first_page")
    private int firstPage;

    @ApiModelProperty(value = "上一页页号", required = true, example = "2")
    @JsonProperty("pre_page")
    private int prePage;

    @ApiModelProperty(value = "下一页页号", required = true, example = "3")
    @JsonProperty("next_page")
    private int nextPage;

    @ApiModelProperty(value = "尾页页号", required = true, example = "3")
    @JsonProperty("last_page")
    private int lastPage;

    @ApiModelProperty(value = "是否为第一页", required = true, example = "false")
    @JsonProperty("is_first_page")
    private boolean isFirstPage;

    @ApiModelProperty(value = "是否为最后一页", required = true, example = "true")
    @JsonProperty("is_last_page")
    private boolean isLastPage;

    @ApiModelProperty(value = "是否有前一页", required = true, example = "true")
    @JsonProperty("has_previous_page")
    private boolean hasPreviousPage;

    @ApiModelProperty(value = "是否有下一页", required = true, example = "false")
    @JsonProperty("has_next_page")
    private boolean hasNextPage;

    @ApiModelProperty(value = "导航页码数", required = true, example = "3")
    @JsonProperty("navigate_pages")
    private int navigatePages;

    @ApiModelProperty(value = "所有导航页码数", required = true, example = "[1,2,3]")
    @JsonProperty("navigatepage_nums")
    private int[] navigatepageNums;
}
