import { useEffect, useState, FC } from "react";
import { NewsCard } from "../NewsCard";
import { KeywordBar } from "../KeywordBar";
import { RelatedKeywordBar } from "../RelatedKeywordBar";
import { getNews, getKeyWordNews } from "@/modules/api";
import { newsResponse } from "./types";
import { useSelector } from "react-redux";
import { RootState } from "@/modules/store";
export const NewsBoard: FC = () => {
  const [subSelect, setSubSelect] = useState(-1);
  const [relSelect, setRelSelect] = useState(0);
  const [keywordTitle, setKeywordTitle] = useState("인기 & 추천");
  const [keywordId, setKeywordId] = useState(-1);
  const [pageNum, setPageNum] = useState(0);
  const [newsObject, setNewsObject] = useState<newsResponse>({
    page: 0,
    last: false,
    newsList: [],
  });
  const keywords = ["인기", "네이버", "비트코인", "이청아", "제페토"];
  const userKeywords = ["SSAFY", "카카오", "메타버스", "남궁민", "블록체인"];
  const { subScribeKwdList } = useSelector((state: RootState) => {
    return {
      subScribeKwdList: state.persistedReducer.keyWordReducer.subScribeKwdList,
    };
  });
  useEffect(() => {
    getNews(0).then((data) => {
      setNewsObject(data);
      console.log(newsObject);
    });
  }, []);
  useEffect(() => {
    setPageNum(0)
    fetchArticle(0)
  }, [keywordTitle]);
  useEffect(() => {
    fetchArticle(pageNum)
    console.log(newsObject)
  }, [pageNum])
  const onPageClick = () => {
    setPageNum((pageNum + 1) % 10)

  }
  const fetchArticle = (pageNum:number) => {
    if(subSelect === -1) {
      getNews(pageNum).then((data) => {
        if (data.newsList.length === 0) {
          setPageNum(0)
        }
        setNewsObject(data);
      });
    }
    else {
      getKeyWordNews(subSelect, pageNum).then((data) => {
        if (data.newsList.length === 0) {
          setPageNum(0)
        }
        console.log(data)
        setNewsObject(data);
      });
    }
  }
  return (
    <div>
      <KeywordBar
        keywordList={subScribeKwdList}
        subSelect={subSelect}
        setSubSelect={setSubSelect}
        setKeywordTitle={setKeywordTitle}
        setKeywordId={setKeywordId}
      />
      <div className="bg-black/50 rounded-lg my-4 mx-2 p-4">
        <div className="text-white text-xl">{keywordTitle}</div>
        <div>
          <RelatedKeywordBar
            keywordList={keywords}
            relSelect={relSelect}
            setRelSelect={setRelSelect}
          />
          <button className="mx-2" onClick={onPageClick}>
          <img className="m-auto" src="refresh_32.png" />
        </button>
        </div>
        <div className="grid grid-cols-3 gap-4">
          {newsObject.newsList.map((list, index) => {
            return (
              <div key={index}>
                <NewsCard newsList={list} />
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
