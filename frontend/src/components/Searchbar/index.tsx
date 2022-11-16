import { useRef } from "react";

export const Searchbar = () => {
  return (
    <form action="https://www.google.com/search" method="GET">
      <div>
        <input
          className="rounded-full my-8 h-10 p-4 mx-2"
          name="q"
          type="text"
          style={{width: 'calc(100% - 16px)'}}
          placeholder="Google 검색 또는 URL 입력"
          aria-label="Recipient's username"
          aria-describedby="button-addon2"
          autoComplete="off"
        />
      </div>
    </form>
  );
};
