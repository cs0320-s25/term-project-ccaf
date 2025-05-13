const HOST = "http://localhost:3232";

async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

export async function addDraft(uid: string, name: string) {
  return await queryAPI("create-draft", {
    userId: uid,
    draftName: name
  });
}


export async function viewDrafts(uid: string) {
    return await queryAPI("view-drafts", {
        userId: uid
    });
}

export async function addToDraft(uid: string, draftId: string, pieceId: string, title: string, price: number, sourceWebsite: string, url: string, size: string, color: string, condition: string, imageUrl: string, tags: string[]) {
  return await queryAPI("save-piece", {
    userId: uid,
    draftId: draftId,
    pieceId: pieceId,
    title: title,
    price: price.toString(),
    sourceWebsite: sourceWebsite,
    url: url,
    imageUrl: imageUrl,
    size: size,
    color: color,
    condition: condition,
    tags: tags.toString()
  });
}

export async function viewPiecesInDraft(uid: string, draftId: string) {
  return await queryAPI("view-piece", {
      userId: uid,
      draftId: draftId
  });
}

export async function removeDraft(uid: string, draftId: string) {
  return await queryAPI("remove-draft", {
    userId: uid,
    draftId: draftId
  });
}

export async function removeFromDraft(uid: string, draftId: string, pieceId: string) {
  return await queryAPI("remove-piece", {
    userId: uid,
    draftId: draftId,
    pieceId: pieceId
  }); 
}